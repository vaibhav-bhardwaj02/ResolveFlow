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

function formatDate(dateStr) {
  if (!dateStr) return '—';
  const d = new Date(dateStr);
  return d.toLocaleDateString(undefined, { year: 'numeric', month: 'short', day: 'numeric' });
}

function badge(text, type) {
  return `<span class="badge badge-${type}">${text}</span>`;
}

document.addEventListener('DOMContentLoaded', () => {
  const user = requireAuth(['CUSTOMER', 'SUPPORT_AGENT']);
  if (!user) return; // requireAuth already redirected

  const isAgent = user.role === 'SUPPORT_AGENT';

  // ---- Nav ----
  document.getElementById('nav-avatar').textContent = (user.firstName?.[0] || '') + (user.lastName?.[0] || '');
  document.getElementById('nav-name').textContent = `${user.firstName} ${user.lastName}`;
  document.getElementById('nav-role').textContent = isAgent ? 'Support Agent' : 'Customer';
  document.getElementById('logout-btn').addEventListener('click', logout);

  // ---- Page copy + "New Complaint" button visibility ----
  const newComplaintBtn = document.getElementById('new-complaint-btn');
  if (isAgent) {
    document.getElementById('page-title').textContent = 'Assigned to me';
    document.getElementById('page-subtitle').textContent = 'Complaints currently assigned to you.';
    document.getElementById('empty-title').textContent = 'Nothing assigned yet';
    document.getElementById('empty-subtitle').textContent = "You'll see complaints here once an admin assigns them to you.";
    newComplaintBtn.style.display = 'none';
  } else {
    document.getElementById('page-title').textContent = 'My Complaints';
    document.getElementById('page-subtitle').textContent = "Track the status of complaints you've raised.";
    document.getElementById('empty-title').textContent = 'No complaints yet';
    document.getElementById('empty-subtitle').textContent = "When you raise a complaint, it'll show up here.";
  }

  // ---- Elements ----
  const loadingText = document.getElementById('loading-text');
  const emptyState = document.getElementById('empty-state');
  const listEl = document.getElementById('complaint-list');

  const newComplaintOverlay = document.getElementById('new-complaint-overlay');
  const newComplaintForm = document.getElementById('new-complaint-form');
  const newComplaintAlert = document.getElementById('new-complaint-alert');
  const newComplaintSubmit = document.getElementById('new-complaint-submit');
  const categorySelect = document.getElementById('categoryId');

  const detailOverlay = document.getElementById('detail-overlay');
  const detailTitle = document.getElementById('detail-title');
  const detailBody = document.getElementById('detail-body');
  const agentUpdateSection = document.getElementById('agent-update-section');
  const updateStatusForm = document.getElementById('update-status-form');
  const updateStatusAlert = document.getElementById('update-status-alert');
  const updateStatusSubmit = document.getElementById('update-status-submit');

  let currentDetailComplaintId = null;
  let allComplaints = [];
  let activeFilter = 'active'; // agent view only: 'active' | 'resolved' | 'all'

  const agentFilterBar = document.getElementById('agent-filter-bar');
  if (isAgent) {
    agentFilterBar.style.display = 'flex';
    agentFilterBar.addEventListener('click', (e) => {
      const btn = e.target.closest('.status-filter__btn');
      if (!btn) return;

      activeFilter = btn.dataset.filter;
      agentFilterBar.querySelectorAll('.status-filter__btn').forEach((b) => b.classList.remove('active'));
      btn.classList.add('active');
      renderList();
    });
  }

  // ---- Load complaints ----
  async function loadComplaints() {
    loadingText.style.display = 'block';
    emptyState.style.display = 'none';
    listEl.innerHTML = '';

    try {
      const endpoint = isAgent ? '/api/complaints/assigned/me' : '/api/complaints/my';
      allComplaints = await Api.get(endpoint) || [];
      loadingText.style.display = 'none';
      renderList();
    } catch (err) {
      loadingText.style.display = 'none';
      emptyState.style.display = 'block';
      document.getElementById('empty-title').textContent = 'Could not load complaints';
      document.getElementById('empty-subtitle').textContent = err.message || 'Please try refreshing the page.';
    }
  }

  const DONE_STATUSES = ['RESOLVED', 'CLOSED'];

  function renderList() {
    listEl.innerHTML = '';

    let filtered = allComplaints;
    if (isAgent && activeFilter === 'active') {
      filtered = allComplaints.filter((c) => !DONE_STATUSES.includes(c.status));
    } else if (isAgent && activeFilter === 'resolved') {
      filtered = allComplaints.filter((c) => DONE_STATUSES.includes(c.status));
    }

    if (filtered.length === 0) {
      emptyState.style.display = 'block';
      if (isAgent && activeFilter === 'active') {
        document.getElementById('empty-title').textContent = 'Nothing active right now';
        document.getElementById('empty-subtitle').textContent = "You're all caught up — resolved complaints are under the \"Resolved\" filter above.";
      } else if (isAgent && activeFilter === 'resolved') {
        document.getElementById('empty-title').textContent = 'Nothing resolved yet';
        document.getElementById('empty-subtitle').textContent = "Complaints you've resolved or closed will show up here.";
      } else if (isAgent) {
        document.getElementById('empty-title').textContent = 'Nothing assigned yet';
        document.getElementById('empty-subtitle').textContent = "You'll see complaints here once an admin assigns them to you.";
      } else {
        document.getElementById('empty-title').textContent = 'No complaints yet';
        document.getElementById('empty-subtitle').textContent = "When you raise a complaint, it'll show up here.";
      }
      return;
    }

    emptyState.style.display = 'none';
    filtered.forEach((c) => renderCard(c));
  }

  function renderCard(c) {
    const card = document.createElement('div');
    card.className = 'complaint-card';
    card.innerHTML = `
      <div class="complaint-card__top">
        <div>
          <div class="complaint-card__title">${escapeHtml(c.title)}</div>
          <div class="complaint-card__number">${escapeHtml(c.complaintNumber)}</div>
        </div>
        <div class="complaint-card__badges">
          ${badge(formatStatus(c.status), STATUS_BADGE[c.status] || 'neutral')}
          ${badge(c.priority, PRIORITY_BADGE[c.priority] || 'neutral')}
        </div>
      </div>
      <div class="complaint-card__meta">
        <span>${escapeHtml(c.categoryName || '—')}</span>
        <span>Raised ${formatDate(c.createdAt)}</span>
        ${c.resolvedAt ? `<span>Resolved ${formatDate(c.resolvedAt)}</span>` : ''}
      </div>
    `;
    card.addEventListener('click', () => openDetail(c.id));
    listEl.appendChild(card);
  }

  function escapeHtml(str) {
    const div = document.createElement('div');
    div.textContent = str ?? '';
    return div.innerHTML;
  }

  // ---- Categories (for the "new complaint" form) ----
  async function loadCategories() {
    try {
      const categories = await Api.get('/api/categories');
      categories
        .filter((cat) => cat.active !== false)
        .forEach((cat) => {
          const opt = document.createElement('option');
          opt.value = cat.id;
          opt.textContent = cat.name;
          categorySelect.appendChild(opt);
        });
    } catch (err) {
      // Non-fatal — the dropdown just stays with only the placeholder option
      console.error('Failed to load categories:', err.message);
    }
  }

  // ---- New complaint modal ----
  if (!isAgent) {
    newComplaintBtn.addEventListener('click', () => {
      newComplaintForm.reset();
      hideAlert(newComplaintAlert);
      clearFieldErrors(newComplaintForm);
      newComplaintOverlay.classList.add('visible');
    });

    document.getElementById('new-complaint-close').addEventListener('click', () => {
      newComplaintOverlay.classList.remove('visible');
    });

    newComplaintOverlay.addEventListener('click', (e) => {
      if (e.target === newComplaintOverlay) newComplaintOverlay.classList.remove('visible');
    });

    newComplaintForm.addEventListener('submit', async (e) => {
      e.preventDefault();
      hideAlert(newComplaintAlert);
      clearFieldErrors(newComplaintForm);

      const payload = {
        title: newComplaintForm.title.value.trim(),
        description: newComplaintForm.description.value.trim(),
        priority: newComplaintForm.priority.value,
        categoryId: Number(newComplaintForm.categoryId.value),
        customerId: user.id,
      };

      newComplaintSubmit.disabled = true;
      newComplaintSubmit.textContent = 'Submitting…';

      try {
        await Api.post('/api/complaints', payload);
        newComplaintOverlay.classList.remove('visible');
        loadComplaints();
      } catch (err) {
        if (err.errors) {
          applyFieldErrors(newComplaintForm, err.errors);
        } else {
          showAlert(newComplaintAlert, err.message || 'Could not submit complaint. Please try again.');
        }
      } finally {
        newComplaintSubmit.disabled = false;
        newComplaintSubmit.textContent = 'Submit complaint';
      }
    });
  }

  // ---- Detail modal ----
  async function openDetail(id) {
    currentDetailComplaintId = id;
    detailBody.innerHTML = '<p class="loading-text">Loading…</p>';
    agentUpdateSection.style.display = 'none';
    detailOverlay.classList.add('visible');

    try {
      const c = await Api.get(`/api/complaints/${id}`);
      detailTitle.textContent = c.title;

      detailBody.innerHTML = `
        <div class="detail-row">
          <span class="detail-row__label">Complaint #</span>
          <span class="detail-row__value">${escapeHtml(c.complaintNumber)}</span>
        </div>
        <div class="detail-row">
          <span class="detail-row__label">Status</span>
          <span class="detail-row__value">${badge(formatStatus(c.status), STATUS_BADGE[c.status] || 'neutral')}</span>
        </div>
        <div class="detail-row">
          <span class="detail-row__label">Priority</span>
          <span class="detail-row__value">${badge(c.priority, PRIORITY_BADGE[c.priority] || 'neutral')}</span>
        </div>
        <div class="detail-row">
          <span class="detail-row__label">Category</span>
          <span class="detail-row__value">${escapeHtml(c.categoryName || '—')}</span>
        </div>
        ${isAgent ? `
        <div class="detail-row">
          <span class="detail-row__label">Customer</span>
          <span class="detail-row__value">${escapeHtml(c.customerName || '—')}</span>
        </div>` : `
        <div class="detail-row">
          <span class="detail-row__label">Assigned to</span>
          <span class="detail-row__value">${escapeHtml(c.assignedAgentName || 'Not yet assigned')}</span>
        </div>`}
        <div class="detail-row">
          <span class="detail-row__label">Raised on</span>
          <span class="detail-row__value">${formatDate(c.createdAt)}</span>
        </div>
        ${c.resolvedAt ? `
        <div class="detail-row">
          <span class="detail-row__label">Resolved on</span>
          <span class="detail-row__value">${formatDate(c.resolvedAt)}</span>
        </div>` : ''}

        <div class="detail-section-label">Description</div>
        <div class="detail-description">${escapeHtml(c.description)}</div>

        ${c.resolutionRemarks ? `
        <div class="detail-section-label">Resolution remarks</div>
        <div class="detail-description">${escapeHtml(c.resolutionRemarks)}</div>` : ''}
      `;

      // Agents get an inline status-update form
      if (isAgent) {
        agentUpdateSection.style.display = 'block';
        updateStatusForm.reset();
        updateStatusForm.status.value = c.status;
        hideAlert(updateStatusAlert);
        clearFieldErrors(updateStatusForm);
      }
    } catch (err) {
      detailBody.innerHTML = `<p class="loading-text">${escapeHtml(err.message || 'Could not load complaint details.')}</p>`;
    }
  }

  document.getElementById('detail-close').addEventListener('click', () => {
    detailOverlay.classList.remove('visible');
  });

  detailOverlay.addEventListener('click', (e) => {
    if (e.target === detailOverlay) detailOverlay.classList.remove('visible');
  });

  if (isAgent) {
    updateStatusForm.addEventListener('submit', async (e) => {
      e.preventDefault();
      hideAlert(updateStatusAlert);
      clearFieldErrors(updateStatusForm);

      const payload = {
        complaintId: currentDetailComplaintId,
        status: updateStatusForm.status.value,
        resolutionRemarks: updateStatusForm.resolutionRemarks.value.trim() || null,
      };

      updateStatusSubmit.disabled = true;
      updateStatusSubmit.textContent = 'Updating…';

      try {
        await Api.patch('/api/complaints/status', payload);
        detailOverlay.classList.remove('visible');
        loadComplaints();
      } catch (err) {
        if (err.errors) {
          applyFieldErrors(updateStatusForm, err.errors);
        } else {
          showAlert(updateStatusAlert, err.message || 'Could not update status. Please try again.');
        }
      } finally {
        updateStatusSubmit.disabled = false;
        updateStatusSubmit.textContent = 'Update status';
      }
    });
  }

  // ---- Init ----
  if (!isAgent) loadCategories();
  loadComplaints();
});