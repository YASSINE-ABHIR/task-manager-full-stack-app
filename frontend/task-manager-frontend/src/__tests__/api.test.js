import { vi, beforeEach, afterEach, describe, test, expect } from 'vitest';
import apiService from '../lib/api';

// Mock fetch globally
const mockFetch = vi.fn();
global.fetch = mockFetch;

// Mock localStorage
const mockLocalStorage = {
  getItem: vi.fn(),
  setItem: vi.fn(),
  removeItem: vi.fn()
};
Object.defineProperty(window, 'localStorage', {
  value: mockLocalStorage
});

describe('ApiService', () => {
  beforeEach(() => {
    vi.clearAllMocks();
    mockLocalStorage.getItem.mockReturnValue(null);
  });

  afterEach(() => {
    vi.restoreAllMocks();
  });

  describe('Authentication methods', () => {
    test('getAuthToken returns token from localStorage', () => {
      mockLocalStorage.getItem.mockReturnValue('test-token');
      
      const token = apiService.getAuthToken();
      
      expect(mockLocalStorage.getItem).toHaveBeenCalledWith('token');
      expect(token).toBe('test-token');
    });

    test('setAuthToken stores token in localStorage', () => {
      apiService.setAuthToken('new-token');
      
      expect(mockLocalStorage.setItem).toHaveBeenCalledWith('token', 'new-token');
    });

    test('removeAuthToken removes token from localStorage', () => {
      apiService.removeAuthToken();
      
      expect(mockLocalStorage.removeItem).toHaveBeenCalledWith('token');
    });

    test('login makes correct API call and stores token', async () => {
      const mockResponse = {
        success: true,
        data: { token: 'login-token', username: 'testuser' }
      };
      
      mockFetch.mockResolvedValue({
        ok: true,
        json: () => Promise.resolve(mockResponse)
      });

      const credentials = { usernameOrEmail: 'testuser', password: 'password' };
      const result = await apiService.login(credentials);

      expect(mockFetch).toHaveBeenCalledWith(
        'http://localhost:8080/api/auth/login',
        {
          method: 'POST',
          headers: { 'Content-Type': 'application/json' },
          body: JSON.stringify(credentials)
        }
      );
      expect(mockLocalStorage.setItem).toHaveBeenCalledWith('token', 'login-token');
      expect(result).toEqual(mockResponse);
    });

    test('register makes correct API call and stores token', async () => {
      const mockResponse = {
        success: true,
        data: { token: 'register-token', username: 'newuser' }
      };
      
      mockFetch.mockResolvedValue({
        ok: true,
        json: () => Promise.resolve(mockResponse)
      });

      const userData = {
        username: 'newuser',
        email: 'new@example.com',
        password: 'password'
      };
      const result = await apiService.register(userData);

      expect(mockFetch).toHaveBeenCalledWith(
        'http://localhost:8080/api/auth/register',
        {
          method: 'POST',
          headers: { 'Content-Type': 'application/json' },
          body: JSON.stringify(userData)
        }
      );
      expect(mockLocalStorage.setItem).toHaveBeenCalledWith('token', 'register-token');
      expect(result).toEqual(mockResponse);
    });

    test('logout removes token from localStorage', async () => {
      mockFetch.mockResolvedValue({
        ok: true,
        json: () => Promise.resolve({ success: true })
      });

      await apiService.logout();

      expect(mockLocalStorage.removeItem).toHaveBeenCalledWith('token');
    });
  });

  describe('Task methods', () => {
    beforeEach(() => {
      mockLocalStorage.getItem.mockReturnValue('test-token');
    });

    test('getTasks makes correct API call with auth headers', async () => {
      const mockResponse = {
        success: true,
        data: [{ id: 1, title: 'Test Task' }]
      };
      
      mockFetch.mockResolvedValue({
        ok: true,
        json: () => Promise.resolve(mockResponse)
      });

      const result = await apiService.getTasks();

      expect(mockFetch).toHaveBeenCalledWith(
        'http://localhost:8080/api/tasks',
        {
          headers: {
            'Content-Type': 'application/json',
            'Authorization': 'Bearer test-token'
          }
        }
      );
      expect(result).toEqual(mockResponse);
    });

    test('getTasks with parameters builds correct query string', async () => {
      mockFetch.mockResolvedValue({
        ok: true,
        json: () => Promise.resolve({ success: true, data: [] })
      });

      const params = { status: 'PENDING', priority: 'HIGH' };
      await apiService.getTasks(params);

      expect(mockFetch).toHaveBeenCalledWith(
        'http://localhost:8080/api/tasks?status=PENDING&priority=HIGH',
        expect.any(Object)
      );
    });

    test('createTask makes correct API call', async () => {
      const mockResponse = {
        success: true,
        data: { id: 1, title: 'New Task' }
      };
      
      mockFetch.mockResolvedValue({
        ok: true,
        json: () => Promise.resolve(mockResponse)
      });

      const taskData = {
        title: 'New Task',
        description: 'Task description',
        status: 'PENDING',
        priority: 'MEDIUM'
      };
      const result = await apiService.createTask(taskData);

      expect(mockFetch).toHaveBeenCalledWith(
        'http://localhost:8080/api/tasks',
        {
          method: 'POST',
          headers: {
            'Content-Type': 'application/json',
            'Authorization': 'Bearer test-token'
          },
          body: JSON.stringify(taskData)
        }
      );
      expect(result).toEqual(mockResponse);
    });

    test('updateTask makes correct API call', async () => {
      const mockResponse = {
        success: true,
        data: { id: 1, title: 'Updated Task' }
      };
      
      mockFetch.mockResolvedValue({
        ok: true,
        json: () => Promise.resolve(mockResponse)
      });

      const taskData = { title: 'Updated Task' };
      const result = await apiService.updateTask(1, taskData);

      expect(mockFetch).toHaveBeenCalledWith(
        'http://localhost:8080/api/tasks/1',
        {
          method: 'PUT',
          headers: {
            'Content-Type': 'application/json',
            'Authorization': 'Bearer test-token'
          },
          body: JSON.stringify(taskData)
        }
      );
      expect(result).toEqual(mockResponse);
    });

    test('deleteTask makes correct API call', async () => {
      const mockResponse = { success: true };
      
      mockFetch.mockResolvedValue({
        ok: true,
        json: () => Promise.resolve(mockResponse)
      });

      const result = await apiService.deleteTask(1);

      expect(mockFetch).toHaveBeenCalledWith(
        'http://localhost:8080/api/tasks/1',
        {
          method: 'DELETE',
          headers: {
            'Content-Type': 'application/json',
            'Authorization': 'Bearer test-token'
          }
        }
      );
      expect(result).toEqual(mockResponse);
    });

    test('searchTasks makes correct API call with encoded keyword', async () => {
      mockFetch.mockResolvedValue({
        ok: true,
        json: () => Promise.resolve({ success: true, data: [] })
      });

      await apiService.searchTasks('test keyword');

      expect(mockFetch).toHaveBeenCalledWith(
        'http://localhost:8080/api/tasks?search=test%20keyword',
        expect.any(Object)
      );
    });
  });

  describe('Error handling', () => {
    test('throws error when API response is not ok', async () => {
      mockFetch.mockResolvedValue({
        ok: false,
        json: () => Promise.resolve({ message: 'API Error' })
      });

      await expect(apiService.getTasks()).rejects.toThrow('API Error');
    });

    test('throws error when fetch fails', async () => {
      mockFetch.mockRejectedValue(new Error('Network Error'));

      await expect(apiService.getTasks()).rejects.toThrow('Network Error');
    });

    test('uses default error message when none provided', async () => {
      mockFetch.mockResolvedValue({
        ok: false,
        json: () => Promise.resolve({})
      });

      await expect(apiService.getTasks()).rejects.toThrow('API request failed');
    });
  });

  describe('Auth headers', () => {
    test('includes auth header when token exists', () => {
      mockLocalStorage.getItem.mockReturnValue('test-token');
      
      const headers = apiService.getAuthHeaders();
      
      expect(headers).toEqual({
        'Content-Type': 'application/json',
        'Authorization': 'Bearer test-token'
      });
    });

    test('excludes auth header when no token exists', () => {
      mockLocalStorage.getItem.mockReturnValue(null);
      
      const headers = apiService.getAuthHeaders();
      
      expect(headers).toEqual({
        'Content-Type': 'application/json'
      });
    });
  });
});

