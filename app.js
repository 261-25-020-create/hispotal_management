const state = {
  tab: "patients",
  data: {
    patients: [],
    doctors: [],
    rooms: [],
    appointments: [],
  },
};

// Maps dashboard tabs to your repo's CSV file paths
const csvFiles = {
  patients: "./data/1_Patient.csv",
  doctors: "./data/2_Doctor.csv",
  appointments: "./data/3_Appoinment.csv",
  rooms: "./data/4_Room.csv",
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
    { key: "patientId", label: "Patient ID" },
    { key: "doctorId", label: "Doctor ID" },
    { key: "date", label: "Date" },
    { key: "type", label: "Type" },
    { key: "status", label: "Status", badge: true },
  ],
};

// Helper function to parse CSV raw text into JS array of objects
function parseCSV(text) {
  const lines = text.trim().split("\n");
  if (lines.length < 2) return [];
  const headers = lines[0].split(",").map((h) => h.trim());

  return lines.slice(1).map((line) => {
    const values = line.split(",").map((v) => v.trim());
    const obj = {};
    headers.forEach((header, i) => {
      obj[header] = values[i] || "";
    });
    return obj;
  });
}

// Loads CSV file for a given tab
async function fetchTabData(tab) {
  try {
    const res = await fetch(csvFiles[tab]);
    if (!res.ok) throw new Error(`HTTP error! Status: ${res.status}`);
    const text = await res.text();
    return parseCSV(text);
  } catch (err) {
    console.error(`Failed to fetch ${tab} data:`, err);
    return [];
  }
}

// Computes metrics directly from fetched tab datasets
async function loadSummary() {
  // Pre-fetch all datasets if not loaded
  for (const tab of ["patients", "doctors", "rooms", "appointments"]) {
    if (state.data[tab].length === 0) {
      state.data[tab] = await fetchTabData(tab);
    }
  }

  const vacantRooms = state.data.rooms.filter(
    (r) => String(r.status).toLowerCase() === "vacant"
  ).length;

  document.getElementById("summary").innerHTML = `
    <div class="card"><div class="value">${state.data.patients.length}</div><div class="label">Patients</div></div>
    <div class="card"><div class="value">${state.data.doctors.length}</div><div class="label">Doctors</div></div>
    <div class="card"><div class="value">${state.data.rooms.length}</div><div class="label">Rooms (${vacantRooms} vacant)</div></div>
    <div class="card"><div class="value">${state.data.appointments.length}</div><div class="label">Appointments</div></div>
  `;
}

async function loadTab(tab) {
  if (state.data[tab].length === 0) {
    state.data[tab] = await fetchTabData(tab);
  }
  renderTable(tab, state.data[tab]);
}

function renderTable(tab, rows) {
  const cols = columns[tab];
  const query = document.getElementById("search").value.trim().toLowerCase();
  const filtered = query
    ? rows.filter((r) =>
        Object.values(r).some((v) => String(v).toLowerCase().includes(query))
      )
    : rows;

  const container = document.getElementById("tableContainer");

  if (!filtered || filtered.length === 0) {
    container.innerHTML = `<div class="empty-state">No records found.</div>`;
    return;
  }

  const thead = `<thead><tr>${cols.map((c) => `<th>${c.label}</th>`).join("")}</tr></thead>`;
  const tbody = `<tbody>${filtered
    .map(
      (row) =>
        `<tr>${cols
          .map((c) => {
            const raw = row[c.key] || "";
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

// Event Listeners
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

// Initial load
(async function init() {
  await loadSummary();
  await loadTab(state.tab);
})();
