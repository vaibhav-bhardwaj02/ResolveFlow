function escapeHtml(str) {
  const div = document.createElement('div');
  div.textContent = str ?? '';
  return div.innerHTML;
}

function formatDate(dateStr) {
  if (!dateStr) return '—';
  const d = new Date(dateStr);
  return d.toLocaleDateString(undefined, { year: 'numeric', month: 'short', day: 'numeric' });
}

const ROLE_BADGE = {
  ADMIN: 'primary',
  SUPPORT_AGENT: 'warning',
  CUSTOMER: 'neutral',
};

const STATUS_BADGE = {
  SUBMITTED: 'warning',
  APPROVED: 'primary',
  REJECTED: 'danger',
  ASSIGNED: 'primary',
  IN_PROGRESS: 'warning',
  RESOLVED: 'success',
  CLOSED: 'neutral',
};

const PRIORITY_BADGE = {
  LOW: 'neutral',
  MEDIUM: 'warning',
  HIGH: 'danger',
};

function formatStatus(status) {
  return status.replace('_', ' ').toLowerCase().replace(/\b\w/g, (c) => c.toUpperCase());
}

function badge(text, type) {
  return `<span class="badge badge-${type}">${text}</span>`;
}

document.addEventListener('DOMContentLoaded', () => {
  const admin = requireAuth(['ADMIN']);
  if (!admin) return; // requireAuth already redirected

  // ---- Sidebar user info + logout ----
  document.getElementById('nav-avatar').textContent = (admin.firstName?.[0] || '') + (admin.lastName?.[0] || '');
  document.getElementById('nav-name').textContent = `${admin.firstName} ${admin.lastName}`;
  document.getElementById('logout-btn').addEventListener('click', logout);

  // ---- Sidebar section switching ----
  const navItems = document.querySelectorAll('.admin-nav__item');
  const sections = document.querySelectorAll('.admin-section');

  navItems.forEach((item) => {
    item.addEventListener('click', () => {
      const target = item.dataset.section;

      navItems.forEach((i) => i.classList.remove('active'));
      item.classList.add('active');

      sections.forEach((s) => s.classList.remove('active'));
      document.getElementById(`section-${target}`).classList.add('active');
    });
  });

  // ---- Users section ----
  const usersLoading = document.getElementById('users-loading');
  const usersTableWrap = document.getElementById('users-table-wrap');
  const usersTableBody = document.getElementById('users-table-body');

  async function loadUsers() {
    usersLoading.style.display = 'block';
    usersLoading.textContent = 'Loading…';
    usersTableWrap.style.display = 'none';

    try {
      const users = await Api.get('/api/admin/users');

      usersLoading.style.display = 'none';
      usersTableWrap.style.display = 'block';
      usersTableBody.innerHTML = '';

      users.forEach((u) => renderUserRow(u));
    } catch (err) {
      usersLoading.textContent = err.message || 'Could not load users.';
    }
  }

  function renderUserRow(u) {
    const row = document.createElement('tr');
    row.innerHTML = `
      <td class="cell-name">${escapeHtml(u.firstName)} ${escapeHtml(u.lastName)}</td>
      <td>${escapeHtml(u.email)}</td>
      <td class="cell-muted">${escapeHtml(u.phoneNumber)}</td>
      <td><span class="badge badge-${ROLE_BADGE[u.role] || 'neutral'}">${escapeHtml(u.role)}</span></td>
      <td>${u.enabled ? '<span class="badge badge-success">Active</span>' : '<span class="badge badge-danger">Disabled</span>'}</td>
      <td class="cell-muted">${formatDate(u.createdAt)}</td>
      <td><button class="btn-icon-danger" data-user-id="${u.id}" data-user-name="${escapeHtml(u.firstName)} ${escapeHtml(u.lastName)}">Delete</button></td>
    `;
    usersTableBody.appendChild(row);
  }

  usersTableBody.addEventListener('click', async (e) => {
    const btn = e.target.closest('.btn-icon-danger');
    if (!btn) return;
    handleDeleteUser(btn, loadUsers);
  });

  // Shared delete-user logic (used by both the Users table and the Agents table,
  // since an agent is deleted the same way — DELETE /api/admin/users/{id})
  async function handleDeleteUser(btn, onSuccess) {
    const userId = btn.dataset.userId;
    const userName = btn.dataset.userName;

    if (!confirm(`Delete ${userName}? This cannot be undone.`)) return;

    btn.disabled = true;
    btn.textContent = 'Deleting…';

    try {
      await Api.delete(`/api/admin/users/${userId}`);
      onSuccess();
    } catch (err) {
      alert(err.message || 'Could not delete user.');
      btn.disabled = false;
      btn.textContent = 'Delete';
    }
  }

  // ---- Agents section ----
  const agentsLoading = document.getElementById('agents-loading');
  const agentsEmpty = document.getElementById('agents-empty');
  const agentsTableWrap = document.getElementById('agents-table-wrap');
  const agentsTableBody = document.getElementById('agents-table-body');

  async function loadAgents() {
    agentsLoading.style.display = 'block';
    agentsLoading.textContent = 'Loading…';
    agentsEmpty.style.display = 'none';
    agentsTableWrap.style.display = 'none';

    try {
      const agents = await Api.get('/api/admin/agents');
      allAgents = agents || [];

      agentsLoading.style.display = 'none';

      if (!agents || agents.length === 0) {
        agentsEmpty.style.display = 'block';
        return;
      }

      agentsTableWrap.style.display = 'block';
      agentsTableBody.innerHTML = '';
      agents.forEach((a) => renderAgentRow(a));
    } catch (err) {
      agentsLoading.textContent = err.message || 'Could not load agents.';
    }
  }

  function renderAgentRow(a) {
    const row = document.createElement('tr');
    row.innerHTML = `
      <td class="cell-name">${escapeHtml(a.firstName)} ${escapeHtml(a.lastName)}</td>
      <td>${escapeHtml(a.email)}</td>
      <td class="cell-muted">${escapeHtml(a.phoneNumber)}</td>
      <td class="cell-muted">${formatDate(a.createdAt)}</td>
      <td><button class="btn-icon-danger" data-user-id="${a.id}" data-user-name="${escapeHtml(a.firstName)} ${escapeHtml(a.lastName)}">Delete</button></td>
    `;
    agentsTableBody.appendChild(row);
  }

  agentsTableBody.addEventListener('click', async (e) => {
    const btn = e.target.closest('.btn-icon-danger');
    if (!btn) return;
    handleDeleteUser(btn, loadAgents);
  });

  // ---- New Agent modal ----
  const newAgentOverlay = document.getElementById('new-agent-overlay');
  const newAgentForm = document.getElementById('new-agent-form');
  const newAgentAlert = document.getElementById('new-agent-alert');
  const newAgentSubmit = document.getElementById('new-agent-submit');

  document.getElementById('new-agent-btn').addEventListener('click', () => {
    newAgentForm.reset();
    hideAlert(newAgentAlert);
    clearFieldErrors(newAgentForm);
    newAgentOverlay.classList.add('visible');
  });

  document.getElementById('new-agent-close').addEventListener('click', () => {
    newAgentOverlay.classList.remove('visible');
  });

  newAgentOverlay.addEventListener('click', (e) => {
    if (e.target === newAgentOverlay) newAgentOverlay.classList.remove('visible');
  });

  newAgentForm.addEventListener('submit', async (e) => {
    e.preventDefault();
    hideAlert(newAgentAlert);
    clearFieldErrors(newAgentForm);

    const payload = {
      firstName: newAgentForm.firstName.value.trim(),
      lastName: newAgentForm.lastName.value.trim(),
      email: newAgentForm.email.value.trim(),
      password: newAgentForm.password.value,
      phoneNumber: newAgentForm.phoneNumber.value.trim(),
    };

    newAgentSubmit.disabled = true;
    newAgentSubmit.textContent = 'Creating…';

    try {
      await Api.post('/api/admin/agents', payload);
      newAgentOverlay.classList.remove('visible');
      loadAgents();
    } catch (err) {
      if (err.errors) {
        applyFieldErrors(newAgentForm, err.errors);
      } else {
        showAlert(newAgentAlert, err.message || 'Could not create agent. Please try again.');
      }
    } finally {
      newAgentSubmit.disabled = false;
      newAgentSubmit.textContent = 'Create agent';
    }
  });

  // ---- Categories section ----
  const categoriesLoading = document.getElementById('categories-loading');
  const categoriesEmpty = document.getElementById('categories-empty');
  const categoriesTableWrap = document.getElementById('categories-table-wrap');
  const categoriesTableBody = document.getElementById('categories-table-body');

  async function loadCategories() {
    categoriesLoading.style.display = 'block';
    categoriesLoading.textContent = 'Loading…';
    categoriesEmpty.style.display = 'none';
    categoriesTableWrap.style.display = 'none';

    try {
      const categories = await Api.get('/api/categories');

      categoriesLoading.style.display = 'none';

      if (!categories || categories.length === 0) {
        categoriesEmpty.style.display = 'block';
        return;
      }

      categoriesTableWrap.style.display = 'block';
      categoriesTableBody.innerHTML = '';
      categories.forEach((c) => renderCategoryRow(c));
    } catch (err) {
      categoriesLoading.textContent = err.message || 'Could not load categories.';
    }
  }

  function renderCategoryRow(c) {
    const row = document.createElement('tr');
    const isActive = c.active !== false;
    row.innerHTML = `
      <td class="cell-name">${escapeHtml(c.name)}</td>
      <td class="cell-muted">${escapeHtml(c.description || '—')}</td>
      <td>${isActive ? '<span class="badge badge-success">Active</span>' : '<span class="badge badge-neutral">Inactive</span>'}</td>
      <td>
        <div class="row-actions">
          <button class="btn-icon" data-action="edit" data-id="${c.id}" data-name="${escapeHtml(c.name)}" data-description="${escapeHtml(c.description || '')}" data-active="${isActive}">Edit</button>
          <button class="btn-icon" data-action="toggle" data-id="${c.id}" data-active="${isActive}">${isActive ? 'Deactivate' : 'Activate'}</button>
          <button class="btn-icon-danger" data-action="delete" data-id="${c.id}" data-name="${escapeHtml(c.name)}">Delete</button>
        </div>
      </td>
    `;
    categoriesTableBody.appendChild(row);
  }

  categoriesTableBody.addEventListener('click', async (e) => {
    const btn = e.target.closest('button[data-action]');
    if (!btn) return;

    const action = btn.dataset.action;
    const id = btn.dataset.id;

    if (action === 'edit') {
      openCategoryModal({ id, name: btn.dataset.name, description: btn.dataset.description, active: btn.dataset.active === 'true' });
    } else if (action === 'toggle') {
      const newActive = btn.dataset.active !== 'true';
      btn.disabled = true;
      try {
        await Api.patch(`/api/categories/${id}/status?active=${newActive}`);
        loadCategories();
      } catch (err) {
        alert(err.message || 'Could not update category status.');
        btn.disabled = false;
      }
    } else if (action === 'delete') {
      if (!confirm(`Delete category "${btn.dataset.name}"? This cannot be undone.`)) return;
      btn.disabled = true;
      btn.textContent = 'Deleting…';
      try {
        await Api.delete(`/api/categories/${id}`);
        loadCategories();
      } catch (err) {
        alert(err.message || 'Could not delete category.');
        btn.disabled = false;
        btn.textContent = 'Delete';
      }
    }
  });

  // ---- New/Edit Category modal ----
  const categoryOverlay = document.getElementById('category-overlay');
  const categoryForm = document.getElementById('category-form');
  const categoryAlert = document.getElementById('category-alert');
  const categorySubmit = document.getElementById('category-submit');
  const categoryModalTitle = document.getElementById('category-modal-title');

  function openCategoryModal(existing) {
    categoryForm.reset();
    hideAlert(categoryAlert);
    clearFieldErrors(categoryForm);

    if (existing) {
      categoryModalTitle.textContent = 'Edit category';
      categoryForm.categoryEditId.value = existing.id;
      categoryForm.name.value = existing.name;
      categoryForm.description.value = existing.description || '';
      categoryForm.categoryEditActive.value = existing.active;
    } else {
      categoryModalTitle.textContent = 'New category';
      categoryForm.categoryEditId.value = '';
      categoryForm.categoryEditActive.value = 'true';
    }

    categoryOverlay.classList.add('visible');
  }

  document.getElementById('new-category-btn').addEventListener('click', () => openCategoryModal(null));

  document.getElementById('category-close').addEventListener('click', () => {
    categoryOverlay.classList.remove('visible');
  });

  categoryOverlay.addEventListener('click', (e) => {
    if (e.target === categoryOverlay) categoryOverlay.classList.remove('visible');
  });

  categoryForm.addEventListener('submit', async (e) => {
    e.preventDefault();
    hideAlert(categoryAlert);
    clearFieldErrors(categoryForm);

    const editId = categoryForm.categoryEditId.value;
    const payload = {
      name: categoryForm.name.value.trim(),
      description: categoryForm.description.value.trim() || null,
      active: categoryForm.categoryEditActive.value === 'true',
    };

    categorySubmit.disabled = true;
    categorySubmit.textContent = 'Saving…';

    try {
      if (editId) {
        await Api.put(`/api/categories/${editId}`, payload);
      } else {
        await Api.post('/api/categories', payload);
      }
      categoryOverlay.classList.remove('visible');
      loadCategories();
    } catch (err) {
      if (err.errors) {
        applyFieldErrors(categoryForm, err.errors);
      } else {
        showAlert(categoryAlert, err.message || 'Could not save category. Please try again.');
      }
    } finally {
      categorySubmit.disabled = false;
      categorySubmit.textContent = 'Save category';
    }
  });

  // ---- Complaints section ----
  const complaintsLoading = document.getElementById('complaints-loading');
  const complaintsEmpty = document.getElementById('complaints-empty');
  const complaintsTableWrap = document.getElementById('complaints-table-wrap');
  const complaintsTableBody = document.getElementById('complaints-table-body');
  const complaintsSearch = document.getElementById('complaints-search');
  const complaintsStatusFilter = document.getElementById('complaints-status-filter');

  let allComplaints = [];
  let allAgents = []; // populated by loadAgents(), reused here for the assign dropdown

  async function loadComplaints() {
    complaintsLoading.style.display = 'block';
    complaintsLoading.textContent = 'Loading…';
    complaintsEmpty.style.display = 'none';
    complaintsTableWrap.style.display = 'none';

    try {
      allComplaints = await Api.get('/api/complaints');
      complaintsLoading.style.display = 'none';
      renderComplaintsList();
    } catch (err) {
      complaintsLoading.textContent = err.message || 'Could not load complaints.';
    }
  }

  function renderComplaintsList() {
    const query = complaintsSearch.value.trim().toLowerCase();
    const statusFilter = complaintsStatusFilter.value;

    const filtered = allComplaints.filter((c) => {
      const matchesQuery = !query
        || c.title.toLowerCase().includes(query)
        || c.complaintNumber.toLowerCase().includes(query);
      const matchesStatus = !statusFilter || c.status === statusFilter;
      return matchesQuery && matchesStatus;
    });

    if (filtered.length === 0) {
      complaintsTableWrap.style.display = 'none';
      complaintsEmpty.style.display = 'block';
      return;
    }

    complaintsEmpty.style.display = 'none';
    complaintsTableWrap.style.display = 'block';
    complaintsTableBody.innerHTML = '';
    filtered.forEach((c) => renderComplaintRow(c));
  }

  function renderComplaintRow(c) {
    const row = document.createElement('tr');
    row.innerHTML = `
      <td class="cell-muted">${escapeHtml(c.complaintNumber)}</td>
      <td class="cell-name">${escapeHtml(c.title)}</td>
      <td>${escapeHtml(c.customerName || '—')}</td>
      <td class="cell-muted">${escapeHtml(c.categoryName || '—')}</td>
      <td>${badge(c.priority, PRIORITY_BADGE[c.priority] || 'neutral')}</td>
      <td>${badge(formatStatus(c.status), STATUS_BADGE[c.status] || 'neutral')}</td>
      <td class="cell-muted">${escapeHtml(c.assignedAgentName || 'Unassigned')}</td>
      <td><button class="btn-icon" data-action="assign" data-id="${c.id}" data-agent-id="${c.assignedAgentId || ''}">${c.assignedAgentId ? 'Reassign' : 'Assign'}</button></td>
    `;
    complaintsTableBody.appendChild(row);
  }

  complaintsSearch.addEventListener('input', renderComplaintsList);
  complaintsStatusFilter.addEventListener('change', renderComplaintsList);

  complaintsTableBody.addEventListener('click', (e) => {
    const btn = e.target.closest('button[data-action="assign"]');
    if (!btn) return;
    openAssignModal(btn.dataset.id, btn.dataset.agentId);
  });

  // ---- Assign Complaint modal ----
  const assignOverlay = document.getElementById('assign-overlay');
  const assignForm = document.getElementById('assign-form');
  const assignAlert = document.getElementById('assign-alert');
  const assignSubmit = document.getElementById('assign-submit');
  const assignAgentSelect = document.getElementById('assignAgentId');

  function openAssignModal(complaintId, currentAgentId) {
    assignForm.reset();
    hideAlert(assignAlert);
    clearFieldErrors(assignForm);
    assignForm.assignComplaintId.value = complaintId;

    // Rebuild the agent dropdown from the latest loaded agents list
    assignAgentSelect.innerHTML = '<option value="">Select an agent</option>';
    allAgents.forEach((a) => {
      const opt = document.createElement('option');
      opt.value = a.id;
      opt.textContent = `${a.firstName} ${a.lastName}`;
      assignAgentSelect.appendChild(opt);
    });

    if (currentAgentId) {
      assignAgentSelect.value = currentAgentId;
    }

    assignOverlay.classList.add('visible');
  }

  document.getElementById('assign-close').addEventListener('click', () => {
    assignOverlay.classList.remove('visible');
  });

  assignOverlay.addEventListener('click', (e) => {
    if (e.target === assignOverlay) assignOverlay.classList.remove('visible');
  });

  assignForm.addEventListener('submit', async (e) => {
    e.preventDefault();
    hideAlert(assignAlert);
    clearFieldErrors(assignForm);

    const complaintId = assignForm.assignComplaintId.value;
    const payload = { agentId: Number(assignForm.agentId.value) };

    assignSubmit.disabled = true;
    assignSubmit.textContent = 'Assigning…';

    try {
      await Api.put(`/api/admin/complaints/${complaintId}/assign`, payload);
      assignOverlay.classList.remove('visible');
      loadComplaints();
    } catch (err) {
      if (err.errors) {
        applyFieldErrors(assignForm, err.errors);
      } else {
        showAlert(assignAlert, err.message || 'Could not assign complaint. Please try again.');
      }
    } finally {
      assignSubmit.disabled = false;
      assignSubmit.textContent = 'Assign';
    }
  });

  // ---- Statistics section ----
  const statsLoading = document.getElementById('stats-loading');
  const statsContent = document.getElementById('stats-content');
  const statsCards = document.getElementById('stats-cards');

  const STATUS_COLOR = {
    SUBMITTED: '#E8912D',
    APPROVED: '#1B4A8C',
    REJECTED: '#E24C4B',
    ASSIGNED: '#1B4A8C',
    IN_PROGRESS: '#E8912D',
    RESOLVED: '#1F9D6E',
    CLOSED: '#6B7280',
  };

  async function loadStats() {
    statsLoading.style.display = 'block';
    statsLoading.textContent = 'Loading…';
    statsContent.style.display = 'none';

    try {
      const stats = await Api.get('/api/admin/reports/stats');
      statsLoading.style.display = 'none';
      statsContent.style.display = 'block';
      renderStatCards(stats);
      renderStatCharts(stats);
    } catch (err) {
      statsLoading.textContent = err.message || 'Could not load statistics.';
    }
  }

  function renderStatCards(stats) {
    const total = Object.values(stats).reduce((sum, n) => sum + n, 0);
    const active = (stats.SUBMITTED || 0) + (stats.APPROVED || 0) + (stats.ASSIGNED || 0) + (stats.IN_PROGRESS || 0);
    const resolved = (stats.RESOLVED || 0) + (stats.CLOSED || 0);
    const rejected = stats.REJECTED || 0;

    statsCards.innerHTML = `
      <div class="stat-card stat-card--primary">
        <div class="stat-card__value">${total}</div>
        <div class="stat-card__label">Total complaints</div>
      </div>
      <div class="stat-card stat-card--warning">
        <div class="stat-card__value">${active}</div>
        <div class="stat-card__label">Active</div>
      </div>
      <div class="stat-card stat-card--success">
        <div class="stat-card__value">${resolved}</div>
        <div class="stat-card__label">Resolved</div>
      </div>
      <div class="stat-card stat-card--danger">
        <div class="stat-card__value">${rejected}</div>
        <div class="stat-card__label">Rejected</div>
      </div>
    `;
  }

  function renderStatCharts(stats) {
    const labels = Object.keys(stats).map(formatStatus);
    const values = Object.values(stats);
    const colors = Object.keys(stats).map((key) => STATUS_COLOR[key] || '#6B7280');

    new Chart(document.getElementById('stats-bar-chart'), {
      type: 'bar',
      data: {
        labels,
        datasets: [{
          data: values,
          backgroundColor: colors,
          borderRadius: 6,
        }],
      },
      options: {
        plugins: { legend: { display: false } },
        scales: {
          y: { beginAtZero: true, ticks: { precision: 0 } },
        },
      },
    });

    const active = (stats.SUBMITTED || 0) + (stats.APPROVED || 0) + (stats.ASSIGNED || 0) + (stats.IN_PROGRESS || 0);
    const resolved = (stats.RESOLVED || 0) + (stats.CLOSED || 0);
    const rejected = stats.REJECTED || 0;

    new Chart(document.getElementById('stats-donut-chart'), {
      type: 'doughnut',
      data: {
        labels: ['Active', 'Resolved', 'Rejected'],
        datasets: [{
          data: [active, resolved, rejected],
          backgroundColor: ['#E8912D', '#1F9D6E', '#E24C4B'],
          borderWidth: 0,
        }],
      },
      options: {
        plugins: { legend: { position: 'bottom' } },
      },
    });
  }

  // ---- Init ----
  loadUsers();
  loadAgents();
  loadCategories();
  loadComplaints();
  loadStats();
});