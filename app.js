const state = {
  tab: "patients",
  data: {
    patients: [],
    doctors: [],
    rooms: [],
    appointments: [],
  },
};

const columns = {
  patients: [
    { key: "id", label: "ID" },
    { key: "firstName", label: "First Name" },
    { key: "lastName", label: "Last Name" },
    { key: "dateOfBirth", label: "DOB" },
    { key: "gender", label: "Gender" },
    { key: "contactNumber", label: "Contact" },
    { key: "roomNumber", label: "Room" },
  ],
  doctors: [
    { key: "id", label: "ID" },
    { key: "firstName", label: "First Name" },
    { key: "lastName", label: "Last Name" },
    { key: "specialization", label: "Specialization" },
    { key: "contactNumber", label: "Contact" },
    { key: "salary", label: "Salary", format: (v) => "$" + Number(v).toLocaleString() },
  ],
  rooms: [
    { key: "id", label: "ID" },
    { key: "roomNumber", label: "Room #" },
    { key: "type", label: "Type" },
    { key: "status", label: "Status", badge: true },
    { key: "rent", label: "Rent", format: (v) => "$" + Number(v).toLocaleString() },
  ],
  appointments: [
    { key: "id", label: "ID" },
    { key: "patientId", label: "Patient" },
    { key: "doctorId", label: "Doctor" },
    { key: "date", label: "Date" },
    { key: "type", label: "Type" },
    { key: "status", label: "Status", badge: true },
  ],
};

async function loadSummary() {
  const res = await fetch("/api/summary");
  const s = await res.json();
  document.getElementById("summary").innerHTML = `
    <div class="card"><div class="value">${s.patients}</div><div class="label">Patients</div></div>
    <div class="card"><div class="value">${s.doctors}</div><div class="label">Doctors</div></div>
    <div class="card"><div class="value">${s.rooms}</div><div class="label">Rooms (${s.vacantRooms} vacant)</div></div>
    <div class="card"><div class="value">${s.appointments}</div><div class="label">Appointments</div></div>
  `;
}

async function loadTab(tab) {
  if (state.data[tab].length === 0) {
    const res = await fetch(`/api/${tab}`);
    state.data[tab] = await res.json();
  }
  renderTable(tab, state.data[tab]);
}

function renderTable(tab, rows) {
  const cols = columns[tab];
  const query = document.getElementById("search").value.trim().toLowerCase();
  const filtered = query
    ? rows.filter((r) =>
        Object.values(r).some((v) =>
          String(v).toLowerCase().includes(query)
        )
      )
    : rows;

  const container = document.getElementById("tableContainer");

  if (filtered.length === 0) {
    container.innerHTML = `<div class="empty-state">No records found.</div>`;
    return;
  }

  const thead = `<thead><tr>${cols.map((c) => `<th>${c.label}</th>`).join("")}</tr></thead>`;
  const tbody = `<tbody>${filtered
    .map(
      (row) =>
        `<tr>${cols
          .map((c) => {
            const raw = row[c.key];
            const value = c.format ? c.format(raw) : raw;
            if (c.badge) {
              return `<td><span class="badge ${String(raw).toLowerCase()}">${value}</span></td>`;
            }
            return `<td>${value}</td>`;
          })
          .join("")}</tr>`
    )
    .join("")}</tbody>`;

  container.innerHTML = `<table>${thead}${tbody}</table>`;
}

document.querySelectorAll(".tab-btn").forEach((btn) => {
  btn.addEventListener("click", () => {
    document.querySelectorAll(".tab-btn").forEach((b) => b.classList.remove("active"));
    btn.classList.add("active");
    state.tab = btn.dataset.tab;
    document.getElementById("search").value = "";
    loadTab(state.tab);
  });
});

document.getElementById("search").addEventListener("input", () => {
  renderTable(state.tab, state.data[state.tab]);
});

loadSummary();
loadTab(state.tab);
