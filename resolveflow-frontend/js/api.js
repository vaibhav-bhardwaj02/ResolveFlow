const Api = {
  /**
   * Core request method.
   * @param {string} endpoint - e.g. '/api/auth/login'
   * @param {object} options - { method, body, auth }
   *   auth: true attaches the stored JWT as a Bearer token (default true)
   */
  async request(endpoint, { method = 'GET', body = null, auth = true } = {}) {
    const headers = {
      'Content-Type': 'application/json',
    };
 
    if (auth) {
      const token = localStorage.getItem(CONFIG.TOKEN_KEY);
      if (token) {
        headers['Authorization'] = `Bearer ${token}`;
      }
    }
 
    let response;
    try {
      response = await fetch(`${CONFIG.API_BASE_URL}${endpoint}`, {
        method,
        headers,
        body: body ? JSON.stringify(body) : undefined,
      });
    } catch (networkErr) {
      // Backend unreachable, CORS blocked, no internet, etc.
      throw { status: 0, message: 'Could not reach the server. Is the backend running?' };
    }
 
    // Session expired / invalid token — send back to login
    if (response.status === 401 && auth) {
      localStorage.removeItem(CONFIG.TOKEN_KEY);
      localStorage.removeItem(CONFIG.USER_KEY);
      if (!window.location.pathname.endsWith('login.html')) {
        window.location.href = 'login.html';
      }
      throw { status: 401, message: 'Session expired. Please log in again.' };
    }
 
    let data = null;
    const text = await response.text();
    if (text) {
      try {
        data = JSON.parse(text);
      } catch {
        data = null;
      }
    }
 
    if (!response.ok) {
      // Backend returns { errors: {...} } for validation, or { message: '...' } for other errors
      throw {
        status: response.status,
        message: data?.message || 'Something went wrong. Please try again.',
        errors: data?.errors || null,
      };
    }
 
    return data;
  },
 
  get(endpoint, options = {}) {
    return this.request(endpoint, { ...options, method: 'GET' });
  },
 
  post(endpoint, body, options = {}) {
    return this.request(endpoint, { ...options, method: 'POST', body });
  },
 
  put(endpoint, body, options = {}) {
    return this.request(endpoint, { ...options, method: 'PUT', body });
  },
 
  patch(endpoint, body, options = {}) {
    return this.request(endpoint, { ...options, method: 'PATCH', body });
  },
 
  delete(endpoint, options = {}) {
    return this.request(endpoint, { ...options, method: 'DELETE' });
  },
};