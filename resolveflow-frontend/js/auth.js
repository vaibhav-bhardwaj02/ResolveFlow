function decodeJwtPayload(token) {
  try {
    const payload = token.split('.')[1];
    const decoded = atob(payload.replace(/-/g, '+').replace(/_/g, '/'));
    return JSON.parse(decoded);
  } catch {
    return null;
  }
}
 
function redirectByRole(role) {
  if (role === 'ADMIN') {
    window.location.href = 'admin.html';
  } else if (role === 'SUPPORT_AGENT') {
    window.location.href = 'dashboard.html';
  } else {
    window.location.href = 'dashboard.html';
  }
}
 
function logout() {
  localStorage.removeItem(CONFIG.TOKEN_KEY);
  localStorage.removeItem(CONFIG.USER_KEY);
  window.location.href = 'login.html';
}
 
function getCurrentUser() {
  const raw = localStorage.getItem(CONFIG.USER_KEY);
  return raw ? JSON.parse(raw) : null;
}
 
/**
 * Call at the top of any protected page. Redirects to login if not
 * authenticated, or if the user's role isn't in allowedRoles.
 */
function requireAuth(allowedRoles) {
  const token = localStorage.getItem(CONFIG.TOKEN_KEY);
  const user = getCurrentUser();
 
  if (!token || !user) {
    window.location.href = 'login.html';
    return null;
  }
 
  if (allowedRoles && !allowedRoles.includes(user.role)) {
    redirectByRole(user.role);
    return null;
  }
 
  return user;
}
 
function showAlert(el, message, type = 'danger') {
  el.textContent = message;
  el.classList.remove('alert-danger', 'alert-success');
  el.classList.add(`alert-${type}`, 'visible');
}
 
function hideAlert(el) {
  el.classList.remove('visible');
  el.textContent = '';
}
 
function clearFieldErrors(form) {
  form.querySelectorAll('.field').forEach((field) => {
    field.classList.remove('has-error');
    const errorEl = field.querySelector('.field-error');
    if (errorEl) errorEl.textContent = '';
  });
}
 
function applyFieldErrors(form, errors) {
  Object.entries(errors).forEach(([fieldName, message]) => {
    const input = form.querySelector(`[name="${fieldName}"]`);
    if (!input) return;
    const field = input.closest('.field');
    field.classList.add('has-error');
    const errorEl = field.querySelector('.field-error');
    if (errorEl) errorEl.textContent = message;
  });
}
 
document.addEventListener('DOMContentLoaded', () => {
  const form = document.getElementById('login-form');
  if (!form) return; // this page isn't login.html — skip entirely, including the redirect check below
 
  // If already logged in, skip the login page
  const existingToken = localStorage.getItem(CONFIG.TOKEN_KEY);
  if (existingToken) {
    const payload = decodeJwtPayload(existingToken);
    if (payload?.role) {
      redirectByRole(payload.role);
      return;
    }
  }
 
  const alertEl = document.getElementById('login-alert');
  const submitBtn = document.getElementById('login-submit');
 
  // Show a success message if redirected here right after registering
  const params = new URLSearchParams(window.location.search);
  if (params.get('registered') === 'true') {
    showAlert(alertEl, 'Account created successfully. Please sign in.', 'success');
  }
 
  form.addEventListener('submit', async (e) => {
    e.preventDefault();
    hideAlert(alertEl);
    clearFieldErrors(form);
 
    const email = form.email.value.trim();
    const password = form.password.value;
 
    submitBtn.disabled = true;
    submitBtn.textContent = 'Signing in…';
 
    try {
      // Matches LoginRequestDTO (email, password) and LoginResponseDTO
      // (token, tokenType, id, firstName, lastName, email, role) exactly.
      const data = await Api.post('/api/auth/login', { email, password }, { auth: false });
 
      localStorage.setItem(CONFIG.TOKEN_KEY, data.token);
      localStorage.setItem(CONFIG.USER_KEY, JSON.stringify({
        id: data.id,
        firstName: data.firstName,
        lastName: data.lastName,
        email: data.email,
        role: data.role,
      }));
 
      redirectByRole(data.role);
    } catch (err) {
      if (err.errors) {
        applyFieldErrors(form, err.errors);
      } else {
        showAlert(alertEl, err.message || 'Login failed. Please check your credentials.');
      }
    } finally {
      submitBtn.disabled = false;
      submitBtn.textContent = 'Sign in';
    }
  });
});
 
/* ==========================================================================
   Registration (register.html)
   ========================================================================== */
 
document.addEventListener('DOMContentLoaded', () => {
  const form = document.getElementById('register-form');
  if (!form) return; // this page isn't register.html — skip
 
  const alertEl = document.getElementById('register-alert');
  const submitBtn = document.getElementById('register-submit');
 
  form.addEventListener('submit', async (e) => {
    e.preventDefault();
    hideAlert(alertEl);
    clearFieldErrors(form);
 
    // Required fields
    const payload = {
      firstName: form.firstName.value.trim(),
      lastName: form.lastName.value.trim(),
      email: form.email.value.trim(),
      password: form.password.value,
      phoneNumber: form.phoneNumber.value.trim(),
    };
 
    // Optional address fields — only include if the user filled them in
    const addressLine1 = form.addressLine1.value.trim();
    const addressLine2 = form.addressLine2.value.trim();
    const city = form.city.value.trim();
    const state = form.state.value.trim();
    const postalCode = form.postalCode.value.trim();
 
    if (addressLine1) payload.addressLine1 = addressLine1;
    if (addressLine2) payload.addressLine2 = addressLine2;
    if (city) payload.city = city;
    if (state) payload.state = state;
    if (postalCode) payload.postalCode = postalCode;
 
    submitBtn.disabled = true;
    submitBtn.textContent = 'Creating account…';
 
    try {
      // POST /api/auth/register returns a UserResponseDTO (no token) —
      // registration does not log the user in automatically.
      await Api.post('/api/auth/register', payload, { auth: false });
 
      // Send them to login with a success flag so login.html can show a message.
      window.location.href = 'login.html?registered=true';
    } catch (err) {
      if (err.errors) {
        applyFieldErrors(form, err.errors);
      } else {
        showAlert(alertEl, err.message || 'Registration failed. Please try again.');
      }
    } finally {
      submitBtn.disabled = false;
      submitBtn.textContent = 'Create account';
    }
  });
});