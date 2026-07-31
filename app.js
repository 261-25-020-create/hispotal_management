const state = {
  tab: "patients",
  data: {
    patients: [],
    doctors: [],
    rooms: [],
    appointments: [],
  },
};

// Maps dashboard tabs to CSV file paths
const csvFiles = {
  patients: "./data/1_Patient.csv",
  doctors: "./data/2_Doctor.csv",
  appointments: "./data/3_Appoinment.csv",
  rooms: "./data/4_Room.csv",
};

// Exact key names matching CSV headers across all tabs
const columns = {
  patients: [
    { key: "PatientID", label: "ID" },
    { key: "FirstName", label: "First Name" },
    { key: "LastName", label: "Last Name" },
    { key: "DateOfBirth", label: "DOB" },
    { key: "Gender", label: "Gender" },
    { key: "ContactNumber", label: "Contact" },
    { key: "RoomNumber", label: "Room" },
  ],
  doctors: [
    { key: "DoctorID", label: "ID" },
    { key: "FirstName", label: "First Name" },
    { key: "LastName", label: "Last Name" },
    { key: "Specialization", label: "Specialization" },
    { key: "ContactNumber", label: "Contact" },
    {
      key: "Salary",
      label: "Salary",
      format: (v) => {
        if (!v) return "";
        const cleaned = String(v).replace(/[^0-9.-]+/g, "");
        const num = Number(cleaned);
        return isNaN(num) ? v : "$" + num.toLocaleString();
      },
    },
  ],
  rooms: [
    { key: "RoomID", label: "Room ID" },
    { key: "RoomNumber", label: "Room #" },
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
      },
    },
  ],
  appointments: [
    { key: "AppointmentID", label: "ID" },
    { key: "PatientID (FK)", label: "Patient ID" },
    { key: "DoctorID (FK)", label: "Doctor ID" },
    { key: "AppointmentDate", label: "Date" },
    { key: "Type", label: "Type" },
    { key: "Status", label: "Status", badge: true },
  ],
};

// Helper: Split single CSV line respecting commas and tabs
function splitCSVLine(line) {
  const delimiter = line.includes("\t") ? "\t" : ",";
  const result = [];
  let current = "";
  let inQuotes = false;

  for (let i = 0; i < line.length; i++) {
    const char = line[i];
    if (char === '"' || char === "'") {
      inQuotes = !inQuotes;
    } else if (char === delimiter && !inQuotes) {
      result.push(current.trim().replace(/^["']|["']$/g, ""));
      current = "";
    } else {
      current += char;
    }
  }
  result.push(current.trim().replace(/^["']|["']$/g, ""));
  return result;
}

// Robust CSV parser function
function parseCSV(text) {
  const lines = text
    .replace(/\r/g, "")
    .split("\n")
    .map((l) => l.trim())
    .filter((l) => l.length > 0);

  if (lines.length < 2) return [];

  const headers = splitCSVLine(lines[0]);

  return lines.slice(1).map((line) => {
    const values = splitCSVLine(line);
    const obj = {};
    headers.forEach((header, i) => {
      obj[header] = values[i] !== undefined ? values[i] : "";
    });
    return obj;
  });
}

// Fetch CSV file for a given tab
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

// Summary card metrics computation
async function loadSummary() {
  for (const tab of ["patients", "doctors", "rooms", "appointments"]) {
    if (state.data[tab].length === 0) {
      state.data[tab] = await fetchTabData(tab);
    }
  }

  const vacantRooms = state.data.rooms.filter((r) => {
    const statusVal = String(r["Status"] || r["status"] || "").trim().toLowerCase();
    return statusVal === "vacant" || statusVal === "available";
  }).length;

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
              return `<td><span class="badge ${String(raw).trim().toLowerCase()}">${value}</span></td>`;
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
