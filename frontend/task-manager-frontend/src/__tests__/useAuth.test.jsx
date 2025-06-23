import { renderHook, act, waitFor } from '@testing-library/react';
import { vi } from 'vitest';
import { AuthProvider, useAuth } from '../hooks/useAuth';

// Mock the API service
vi.mock('../lib/api', () => ({
  default: {
    getAuthToken: vi.fn(),
    getCurrentUser: vi.fn(),
    removeAuthToken: vi.fn(),
    login: vi.fn(),
    register: vi.fn(),
    logout: vi.fn()
  }
}));

// Import the mocked API service
import apiService from '../lib/api';

const wrapper = ({ children }) => <AuthProvider>{children}</AuthProvider>;

describe('useAuth Hook', () => {
  beforeEach(() => {
    vi.clearAllMocks();
  });

  test('initializes with loading state', () => {
    apiService.getAuthToken.mockReturnValue(null);
    
    const { result } = renderHook(() => useAuth(), { wrapper });
    
    expect(result.current.loading).toBe(true);
    expect(result.current.isAuthenticated).toBe(false);
    expect(result.current.user).toBe(null);
  });

  test('sets authenticated state when valid token exists', async () => {
    apiService.getAuthToken.mockReturnValue('valid-token');
    apiService.getCurrentUser.mockResolvedValue({
      success: true,
      data: { id: 1, username: 'testuser', email: 'test@example.com' }
    });

    const { result } = renderHook(() => useAuth(), { wrapper });

    await waitFor(() => {
      expect(result.current.loading).toBe(false);
    });

    expect(result.current.isAuthenticated).toBe(true);
    expect(result.current.user).toEqual({
      id: 1,
      username: 'testuser',
      email: 'test@example.com'
    });
  });

  test('removes invalid token and sets unauthenticated state', async () => {
    apiService.getAuthToken.mockReturnValue('invalid-token');
    apiService.getCurrentUser.mockResolvedValue({
      success: false,
      message: 'Invalid token'
    });

    const { result } = renderHook(() => useAuth(), { wrapper });

    await waitFor(() => {
      expect(result.current.loading).toBe(false);
    });

    expect(apiService.removeAuthToken).toHaveBeenCalled();
    expect(result.current.isAuthenticated).toBe(false);
    expect(result.current.user).toBe(null);
  });

  test('handles network error during auth check', async () => {
    apiService.getAuthToken.mockReturnValue('some-token');
    apiService.getCurrentUser.mockRejectedValue(new Error('Network error'));

    const { result } = renderHook(() => useAuth(), { wrapper });

    await waitFor(() => {
      expect(result.current.loading).toBe(false);
    });

    expect(apiService.removeAuthToken).toHaveBeenCalled();
    expect(result.current.isAuthenticated).toBe(false);
    expect(result.current.user).toBe(null);
  });

  test('login function works correctly', async () => {
    apiService.getAuthToken.mockReturnValue(null);
    apiService.login.mockResolvedValue({
      success: true,
      data: { id: 1, username: 'testuser', token: 'new-token' }
    });

    const { result } = renderHook(() => useAuth(), { wrapper });

    await waitFor(() => {
      expect(result.current.loading).toBe(false);
    });

    let loginResult;
    await act(async () => {
      loginResult = await result.current.login({
        usernameOrEmail: 'testuser',
        password: 'password'
      });
    });

    expect(loginResult.success).toBe(true);
    expect(result.current.isAuthenticated).toBe(true);
    expect(result.current.user).toEqual({
      id: 1,
      username: 'testuser',
      token: 'new-token'
    });
  });

  test('login function handles failure', async () => {
    apiService.getAuthToken.mockReturnValue(null);
    apiService.login.mockResolvedValue({
      success: false,
      message: 'Invalid credentials'
    });

    const { result } = renderHook(() => useAuth(), { wrapper });

    await waitFor(() => {
      expect(result.current.loading).toBe(false);
    });

    let loginResult;
    await act(async () => {
      loginResult = await result.current.login({
        usernameOrEmail: 'testuser',
        password: 'wrongpassword'
      });
    });

    expect(loginResult.success).toBe(false);
    expect(loginResult.message).toBe('Invalid credentials');
    expect(result.current.isAuthenticated).toBe(false);
    expect(result.current.user).toBe(null);
  });

  test('register function works correctly', async () => {
    apiService.getAuthToken.mockReturnValue(null);
    apiService.register.mockResolvedValue({
      success: true,
      data: { id: 1, username: 'newuser', token: 'new-token' }
    });

    const { result } = renderHook(() => useAuth(), { wrapper });

    await waitFor(() => {
      expect(result.current.loading).toBe(false);
    });

    let registerResult;
    await act(async () => {
      registerResult = await result.current.register({
        username: 'newuser',
        email: 'new@example.com',
        password: 'password'
      });
    });

    expect(registerResult.success).toBe(true);
    expect(result.current.isAuthenticated).toBe(true);
    expect(result.current.user).toEqual({
      id: 1,
      username: 'newuser',
      token: 'new-token'
    });
  });

  test('logout function works correctly', async () => {
    // Start with authenticated state
    apiService.getAuthToken.mockReturnValue('valid-token');
    apiService.getCurrentUser.mockResolvedValue({
      success: true,
      data: { id: 1, username: 'testuser' }
    });

    const { result } = renderHook(() => useAuth(), { wrapper });

    await waitFor(() => {
      expect(result.current.isAuthenticated).toBe(true);
    });

    // Logout
    await act(async () => {
      await result.current.logout();
    });

    expect(apiService.logout).toHaveBeenCalled();
    expect(result.current.isAuthenticated).toBe(false);
    expect(result.current.user).toBe(null);
  });

  test('logout handles API error gracefully', async () => {
    // Start with authenticated state
    apiService.getAuthToken.mockReturnValue('valid-token');
    apiService.getCurrentUser.mockResolvedValue({
      success: true,
      data: { id: 1, username: 'testuser' }
    });
    apiService.logout.mockRejectedValue(new Error('Logout failed'));

    const { result } = renderHook(() => useAuth(), { wrapper });

    await waitFor(() => {
      expect(result.current.isAuthenticated).toBe(true);
    });

    // Logout should still work even if API call fails
    await act(async () => {
      await result.current.logout();
    });

    expect(result.current.isAuthenticated).toBe(false);
    expect(result.current.user).toBe(null);
  });

  test('throws error when used outside AuthProvider', () => {
    // Mock console.error to avoid noise in test output
    const consoleSpy = vi.spyOn(console, 'error').mockImplementation(() => {});
    
    expect(() => {
      renderHook(() => useAuth());
    }).toThrow('useAuth must be used within an AuthProvider');
    
    consoleSpy.mockRestore();
  });
});

