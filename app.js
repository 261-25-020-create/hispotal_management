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

// Keys updated to match exact CSV header names
const columns = {
  patients: [
    { key: "ID", label: "ID" },
    { key: "First Name", label: "First Name" },
    { key: "Last Name", label: "Last Name" },
    { key: "DOB", label: "DOB" },
    { key: "Gender", label: "Gender" },
    { key: "Contact", label: "Contact" },
    { key: "Room", label: "Room" },
  ],
  doctors: [
    { key: "ID", label: "ID" },
    { key: "First Name", label: "First Name" },
    { key: "Last Name", label: "Last Name" },
    { key: "Specialization", label: "Specialization" },
    { key: "Contact", label: "Contact" },
    { 
      key: "Salary", 
      label: "Salary", 
      format: (v) => {
        if (!v) return "";
        // Remove commas or dollar signs before converting to Number
        const cleaned = String(v).replace(/[^0-9.-]+/g, "");
        const num = Number(cleaned);
        return isNaN(num) ? v : "$" + num.toLocaleString();
      } 
    },
  ],
  rooms: [
    { key: "ID", label: "ID" },
    { key: "Room #", label: "Room #" },
    { key: "Type", label: "Type" },
    { key: "Status", label: "Status", badge: true },
    { 
      key: "Rent", 
      label: "Rent", 
      format: (v) => {
        if (!v) return "";
        const cleaned = String(v).replace(/[^0-9.-]+/g, "");
        const num = Number(cleaned);
        return isNaN(num) ? v : "$" + num.toLocaleString();
      } 
    },
  ],
  appointments: [
    { key: "ID", label: "ID" },
    { key: "Patient ID", label: "Patient ID" },
    { key: "Doctor ID", label: "Doctor ID" },
    { key: "Date", label: "Date" },
    { key: "Type", label: "Type" },
    { key: "Status", label: "Status", badge: true },
  ],
};

// Helper function to parse CSV raw text into JS array of objects
function parseCSV(text) {
  // Normalize Windows (\r\n) and Unix (\n) line endings
  const lines = text.replace(/\r/g, "").trim().split("\n");
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
    (r) => String(r.Status || r.status).toLowerCase() === "vacant"
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
            const raw = row[c.key] !== undefined ? row[c.key] : "";
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
