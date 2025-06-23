import { render, screen, fireEvent, waitFor } from '@testing-library/react';
import { vi } from 'vitest';
import TaskForm from '../components/TaskForm';

// Mock the API service
vi.mock('../lib/api', () => ({
  default: {
    createTask: vi.fn(),
    updateTask: vi.fn()
  }
}));

// Import the mocked API service
import apiService from '../lib/api';

describe('TaskForm Component', () => {
  const mockOnClose = vi.fn();
  const mockOnSave = vi.fn();

  beforeEach(() => {
    vi.clearAllMocks();
  });

  test('renders create task form', () => {
    render(
      <TaskForm 
        task={null} 
        onClose={mockOnClose} 
        onSave={mockOnSave} 
      />
    );
    
    expect(screen.getByText('Create New Task')).toBeInTheDocument();
    expect(screen.getByText('Add a new task to your list')).toBeInTheDocument();
    expect(screen.getByLabelText('Title *')).toBeInTheDocument();
    expect(screen.getByLabelText('Description')).toBeInTheDocument();
    expect(screen.getByLabelText('Status')).toBeInTheDocument();
    expect(screen.getByLabelText('Priority')).toBeInTheDocument();
    expect(screen.getByLabelText('Due Date')).toBeInTheDocument();
    expect(screen.getByRole('button', { name: 'Create Task' })).toBeInTheDocument();
  });

  test('renders edit task form with existing data', () => {
    const existingTask = {
      id: 1,
      title: 'Existing Task',
      description: 'Existing Description',
      status: 'IN_PROGRESS',
      priority: 'HIGH',
      dueDate: '2025-12-31'
    };

    render(
      <TaskForm 
        task={existingTask} 
        onClose={mockOnClose} 
        onSave={mockOnSave} 
      />
    );
    
    expect(screen.getByText('Edit Task')).toBeInTheDocument();
    expect(screen.getByDisplayValue('Existing Task')).toBeInTheDocument();
    expect(screen.getByDisplayValue('Existing Description')).toBeInTheDocument();
    expect(screen.getByDisplayValue('IN_PROGRESS')).toBeInTheDocument();
    expect(screen.getByDisplayValue('HIGH')).toBeInTheDocument();
    expect(screen.getByDisplayValue('2025-12-31')).toBeInTheDocument();
    expect(screen.getByRole('button', { name: 'Update Task' })).toBeInTheDocument();
  });

  test('validates required title field', async () => {
    render(
      <TaskForm 
        task={null} 
        onClose={mockOnClose} 
        onSave={mockOnSave} 
      />
    );
    
    const submitButton = screen.getByRole('button', { name: 'Create Task' });
    fireEvent.click(submitButton);

    // HTML5 validation should prevent submission
    const titleInput = screen.getByLabelText('Title *');
    expect(titleInput).toBeInvalid();
  });

  test('handles form submission for new task', async () => {
    apiService.createTask.mockResolvedValue({ success: true });

    render(
      <TaskForm 
        task={null} 
        onClose={mockOnClose} 
        onSave={mockOnSave} 
      />
    );
    
    const titleInput = screen.getByLabelText('Title *');
    const descriptionInput = screen.getByLabelText('Description');
    const submitButton = screen.getByRole('button', { name: 'Create Task' });

    fireEvent.change(titleInput, { target: { value: 'New Task' } });
    fireEvent.change(descriptionInput, { target: { value: 'New Description' } });
    fireEvent.click(submitButton);

    await waitFor(() => {
      expect(apiService.createTask).toHaveBeenCalledWith({
        title: 'New Task',
        description: 'New Description',
        status: 'PENDING',
        priority: 'MEDIUM',
        dueDate: null
      });
    });

    expect(mockOnSave).toHaveBeenCalled();
  });

  test('handles form submission for existing task', async () => {
    apiService.updateTask.mockResolvedValue({ success: true });

    const existingTask = {
      id: 1,
      title: 'Existing Task',
      description: 'Existing Description',
      status: 'PENDING',
      priority: 'MEDIUM',
      dueDate: '2025-12-31'
    };

    render(
      <TaskForm 
        task={existingTask} 
        onClose={mockOnClose} 
        onSave={mockOnSave} 
      />
    );
    
    const titleInput = screen.getByLabelText('Title *');
    const submitButton = screen.getByRole('button', { name: 'Update Task' });

    fireEvent.change(titleInput, { target: { value: 'Updated Task' } });
    fireEvent.click(submitButton);

    await waitFor(() => {
      expect(apiService.updateTask).toHaveBeenCalledWith(1, {
        title: 'Updated Task',
        description: 'Existing Description',
        status: 'PENDING',
        priority: 'MEDIUM',
        dueDate: '2025-12-31'
      });
    });

    expect(mockOnSave).toHaveBeenCalled();
  });

  test('displays error message on API failure', async () => {
    apiService.createTask.mockResolvedValue({ 
      success: false, 
      message: 'Failed to create task' 
    });

    render(
      <TaskForm 
        task={null} 
        onClose={mockOnClose} 
        onSave={mockOnSave} 
      />
    );
    
    const titleInput = screen.getByLabelText('Title *');
    const submitButton = screen.getByRole('button', { name: 'Create Task' });

    fireEvent.change(titleInput, { target: { value: 'New Task' } });
    fireEvent.click(submitButton);

    await waitFor(() => {
      expect(screen.getByText('Failed to create task')).toBeInTheDocument();
    });

    expect(mockOnSave).not.toHaveBeenCalled();
  });

  test('closes form when close button is clicked', () => {
    render(
      <TaskForm 
        task={null} 
        onClose={mockOnClose} 
        onSave={mockOnSave} 
      />
    );
    
    const closeButton = screen.getByRole('button', { name: '' }); // X button
    fireEvent.click(closeButton);

    expect(mockOnClose).toHaveBeenCalled();
  });

  test('closes form when cancel button is clicked', () => {
    render(
      <TaskForm 
        task={null} 
        onClose={mockOnClose} 
        onSave={mockOnSave} 
      />
    );
    
    const cancelButton = screen.getByRole('button', { name: 'Cancel' });
    fireEvent.click(cancelButton);

    expect(mockOnClose).toHaveBeenCalled();
  });

  test('disables form during submission', async () => {
    apiService.createTask.mockImplementation(() => new Promise(resolve => setTimeout(resolve, 100)));

    render(
      <TaskForm 
        task={null} 
        onClose={mockOnClose} 
        onSave={mockOnSave} 
      />
    );
    
    const titleInput = screen.getByLabelText('Title *');
    const submitButton = screen.getByRole('button', { name: 'Create Task' });

    fireEvent.change(titleInput, { target: { value: 'New Task' } });
    fireEvent.click(submitButton);

    await waitFor(() => {
      expect(titleInput).toBeDisabled();
      expect(submitButton).toBeDisabled();
      expect(screen.getByText('Saving...')).toBeInTheDocument();
    });
  });
});

