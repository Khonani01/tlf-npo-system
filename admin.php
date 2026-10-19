<?php
// admin.php - the admin panel. Only logged-in admins get past the first line.
require_once "admin_check.php";   // not an admin? you are sent to login.html
require_once "db_connect.php";

// A secret token that proves a button click came from THIS page (blocks "CSRF" attacks).
if (empty($_SESSION["csrf"])) {
    $_SESSION["csrf"] = bin2hex(random_bytes(32));
}

// Friendly names for the codes stored in the database (same as the volunteer form dropdowns)
$skillLabels = [
    "teaching" => "Teaching / Tutoring", "mentoring" => "Mentoring / Coaching",
    "events" => "Event Coordination", "admin" => "Administration",
    "health" => "Healthcare / First Aid", "construction" => "Construction / Maintenance",
    "communications" => "Communications / Media", "it" => "IT / Technology", "other" => "Other"
];
$eventLabels = [
    "summit" => "Youth Leadership Summit 2026", "cleanup" => "Community Clean-Up Drive",
    "mentorship" => "Education Mentorship Programme", "general" => "General Volunteer"
];

// ---- Handle Approve / Decline / Delete button clicks ----
if ($_SERVER["REQUEST_METHOD"] === "POST") {
    $okToken = hash_equals($_SESSION["csrf"], $_POST["csrf"] ?? "");
    $id      = (int)($_POST["volunteerID"] ?? 0);
    $action  = $_POST["action"] ?? "";
    $result  = "Something went wrong. Please try again.";

    if ($okToken && $id > 0) {
        if ($action === "approve" || $action === "decline") {
            $newStatus = ($action === "approve") ? "approved" : "declined";
            $stmt = $conn->prepare("UPDATE volunteers SET status = ?, reviewedBy = ? WHERE volunteerID = ?");
            $stmt->bind_param("sii", $newStatus, $_SESSION["userID"], $id);
            if ($stmt->execute()) { $result = "Application " . $newStatus . "."; }
        } elseif ($action === "delete") {
            $stmt = $conn->prepare("DELETE FROM volunteers WHERE volunteerID = ?");
            $stmt->bind_param("i", $id);
            if ($stmt->execute()) { $result = "Application deleted."; }
        }
    }
    $_SESSION["flash"] = $result;
    header("Location: admin.php?tab=volunteers");   // redirect so refreshing doesn't repeat the action
    exit;
}

$flash = $_SESSION["flash"] ?? "";
unset($_SESSION["flash"]);

// ---- Load the volunteers list (with optional search and status filter) ----
$search       = trim($_GET["q"] ?? "");
$statusFilter = $_GET["status"] ?? "";
if (!in_array($statusFilter, ["pending", "approved", "declined"], true)) { $statusFilter = ""; }

$sql    = "SELECT volunteerID, fullName, email, phone, skill, eventChoice, message, status, submittedAt FROM volunteers WHERE 1=1";
$types  = "";
$params = [];
if ($search !== "") {
    $sql .= " AND (fullName LIKE ? OR email LIKE ?)";
    $like = "%" . $search . "%";
    $types .= "ss"; $params[] = $like; $params[] = $like;
}
if ($statusFilter !== "") {
    $sql .= " AND status = ?";
    $types .= "s"; $params[] = $statusFilter;
}
$sql .= " ORDER BY submittedAt DESC";

$stmt = $conn->prepare($sql);
if ($types !== "") { $stmt->bind_param($types, ...$params); }
$stmt->execute();
$volunteers = $stmt->get_result()->fetch_all(MYSQLI_ASSOC);

$totalVolunteers = (int)$conn->query("SELECT COUNT(*) AS c FROM volunteers")->fetch_assoc()["c"];
?>
<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>Admin Panel | Tshwane Leadership Foundation</title>
  <link rel="stylesheet" href="style.css">
  <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/7.0.1/css/all.min.css">
  <style>
    .vol-badge { padding: 3px 10px; border-radius: 12px; font-size: 13px; font-weight: bold; }
    .vol-pending  { background: rgba(255,165,0,0.2); color: orange; }
    .vol-approved { background: rgba(46,204,113,0.2); color: #2ecc71; }
    .vol-declined { background: rgba(231,76,60,0.2); color: #e74c3c; }
    .vol-btn { border: none; padding: 5px 10px; border-radius: 5px; cursor: pointer; font-size: 13px; margin: 2px 2px 2px 0; color: white; }
    .vol-btn-ok   { background: #2ecc71; }
    .vol-btn-warn { background: #e67e22; }
    .vol-btn-del  { background: #e74c3c; }
  </style>
</head>
<body>

<header>
<div class="nav-container">
  <img src="images/logo.jpeg" alt="Tshwane Leadership logo" class="logo">
  <nav class="navbar">
    <ul>
      <li><a href="index.html">Home</a></li>
      <li><a href="about.html">About</a></li>
      <li><a href="news.html">News & Events</a></li>
      <li><a href="reports.html">Reports</a></li>
      <li><a href="contact.html">Get Involved</a></li>
      <li><a href="login.html" style="color:orange">Admin</a></li>
    </ul>
  </nav>
  <button class="hamburger" id="hamburger" onclick="toggleMenu()">
    <span></span><span></span><span></span>
  </button>
</div>
<nav class="mobile-nav" id="mobile-nav">
  <a href="index.html">Home</a>
  <a href="about.html">About</a>
  <a href="news.html">News & Events</a>
  <a href="reports.html">Reports</a>
  <a href="contact.html">Get Involved</a>
  <a href="login.html">Admin</a>
</nav>
</header>

<!-- ADMIN DASHBOARD (hidden until login) -->
<div id="admin-dashboard">

<section class="admin-dashboard-section">

  <div class="admin-topbar">
    <h1><i class="fa-solid fa-gauge"></i> Admin Dashboard</h1>
    <a href="logout.php" class="admin-logout-btn" style="text-decoration:none;"><i class="fa-solid fa-right-from-bracket"></i> Logout</a>
  </div>

  <!-- STATS -->
  <div class="admin-stats">
    <div class="admin-stat-card"><i class="fa-solid fa-newspaper"></i><h3 id="stat-news">3</h3><p>News Articles</p></div>
    <div class="admin-stat-card"><i class="fa-solid fa-calendar"></i><h3 id="stat-events">3</h3><p>Events</p></div>
    <div class="admin-stat-card"><i class="fa-solid fa-hands-helping"></i><h3 id="stat-volunteers"><?php echo (int)$totalVolunteers; ?></h3><p>Volunteer Applications</p></div>
    <div class="admin-stat-card"><i class="fa-solid fa-file-pdf"></i><h3>3</h3><p>Reports</p></div>
  </div>

  <!-- TABS -->
  <div class="admin-tabs">
    <button class="admin-tab active" onclick="openAdminTab('news-manage', this)"><i class="fa-solid fa-newspaper"></i> News</button>
    <button class="admin-tab" onclick="openAdminTab('events-manage', this)"><i class="fa-solid fa-calendar"></i> Events</button>
    <button class="admin-tab" onclick="openAdminTab('volunteers-manage', this)"><i class="fa-solid fa-people-group"></i> Volunteers</button>
    <button class="admin-tab" onclick="openAdminTab('reports-manage', this)"><i class="fa-solid fa-file-pdf"></i> Reports</button>
  </div>

  <!-- NEWS MANAGEMENT -->
  <div id="news-manage" class="admin-panel-tab">
    <div class="admin-panel-header">
      <h2>Manage News Articles</h2>
      <button onclick="toggleForm('news-form')" class="add-btn"><i class="fa-solid fa-plus"></i> Add Article</button>
    </div>
    <div id="news-form" class="admin-form-card" style="display:none;">
      <h3>Add New Article</h3>
      <input type="text" id="news-title" placeholder="Article Title" />
      <textarea id="news-body" placeholder="Article body text..." rows="4"></textarea>
      <input type="date" id="news-date" />
      <button onclick="addNews()" class="save-btn"><i class="fa-solid fa-floppy-disk"></i> Save Article</button>
    </div>
    <table class="admin-table">
      <thead><tr><th>Title</th><th>Date</th><th>Actions</th></tr></thead>
      <tbody id="news-table-body">
        <tr><td>Youth Leadership Workshop</td><td>15 Mar 2026</td><td><button class="edit-btn" onclick="alert('Edit feature: connect to Firebase to enable live editing.')"><i class="fa-solid fa-pen"></i> Edit</button> <button class="delete-btn" onclick="deleteRow(this)"><i class="fa-solid fa-trash"></i> Delete</button></td></tr>
        <tr><td>Community Development Program</td><td>10 Mar 2026</td><td><button class="edit-btn" onclick="alert('Edit feature: connect to Firebase to enable live editing.')"><i class="fa-solid fa-pen"></i> Edit</button> <button class="delete-btn" onclick="deleteRow(this)"><i class="fa-solid fa-trash"></i> Delete</button></td></tr>
        <tr><td>Education Support Initiative</td><td>5 Mar 2026</td><td><button class="edit-btn" onclick="alert('Edit feature: connect to Firebase to enable live editing.')"><i class="fa-solid fa-pen"></i> Edit</button> <button class="delete-btn" onclick="deleteRow(this)"><i class="fa-solid fa-trash"></i> Delete</button></td></tr>
      </tbody>
    </table>
  </div>

  <!-- EVENTS MANAGEMENT -->
  <div id="events-manage" class="admin-panel-tab" style="display:none;">
    <div class="admin-panel-header">
      <h2>Manage Events</h2>
      <button onclick="toggleForm('event-form')" class="add-btn"><i class="fa-solid fa-plus"></i> Add Event</button>
    </div>
    <div id="event-form" class="admin-form-card" style="display:none;">
      <h3>Add New Event</h3>
      <input type="text" id="event-title" placeholder="Event Title" />
      <textarea id="event-desc" placeholder="Event description..." rows="3"></textarea>
      <input type="datetime-local" id="event-date" />
      <input type="text" id="event-location" placeholder="Venue / Location" />
      <button onclick="addEvent()" class="save-btn"><i class="fa-solid fa-floppy-disk"></i> Save Event</button>
    </div>
    <table class="admin-table">
      <thead><tr><th>Title</th><th>Date</th><th>Location</th><th>Actions</th></tr></thead>
      <tbody id="events-table-body">
        <tr><td>Youth Leadership Summit 2026</td><td>12 Apr 2026, 09:00</td><td>TLF Community Hall</td><td><button class="edit-btn" onclick="alert('Edit: connect Firebase.')"><i class="fa-solid fa-pen"></i> Edit</button> <button class="delete-btn" onclick="deleteRow(this)"><i class="fa-solid fa-trash"></i> Delete</button></td></tr>
        <tr><td>Community Clean-Up Drive</td><td>25 Apr 2026, 07:00</td><td>Marabastad Park</td><td><button class="edit-btn" onclick="alert('Edit: connect Firebase.')"><i class="fa-solid fa-pen"></i> Edit</button> <button class="delete-btn" onclick="deleteRow(this)"><i class="fa-solid fa-trash"></i> Delete</button></td></tr>
        <tr><td>Education Mentorship Programme</td><td>3 May 2026, 10:00</td><td>Soshanguve Centre</td><td><button class="edit-btn" onclick="alert('Edit: connect Firebase.')"><i class="fa-solid fa-pen"></i> Edit</button> <button class="delete-btn" onclick="deleteRow(this)"><i class="fa-solid fa-trash"></i> Delete</button></td></tr>
      </tbody>
    </table>
  </div>

  <!-- VOLUNTEERS MANAGEMENT (real data from the database) -->
  <div id="volunteers-manage" class="admin-panel-tab" style="display:none;">
    <div class="admin-panel-header">
      <h2>Volunteer Applications</h2>
    </div>

    <?php if ($flash): ?>
      <p style="color:#7CFC9A; margin-bottom:12px;"><?php echo htmlspecialchars($flash); ?></p>
    <?php endif; ?>

    <!-- Search and filter -->
    <form method="get" action="admin.php" style="display:flex; gap:10px; flex-wrap:wrap; margin-bottom:15px;">
      <input type="hidden" name="tab" value="volunteers">
      <input type="text" name="q" value="<?php echo htmlspecialchars($search); ?>" placeholder="Search name or email" style="flex:1; min-width:180px; padding:10px; border-radius:6px; border:1px solid #555; background:#222; color:white;">
      <select name="status" style="padding:10px; border-radius:6px; border:1px solid #555; background:#222; color:white;">
        <option value="">All statuses</option>
        <?php foreach (["pending","approved","declined"] as $st): ?>
          <option value="<?php echo $st; ?>" <?php echo $statusFilter === $st ? "selected" : ""; ?>><?php echo ucfirst($st); ?></option>
        <?php endforeach; ?>
      </select>
      <button type="submit" class="admin-logout-btn">Search</button>
      <a href="admin.php?tab=volunteers" style="color:#aaa; align-self:center;">Clear</a>
    </form>

    <div style="overflow-x:auto;">
    <table class="admin-table">
      <thead><tr><th>Name</th><th>Email</th><th>Phone</th><th>Skill</th><th>Event</th><th>Applied</th><th>Status</th><th>Actions</th></tr></thead>
      <tbody>
      <?php if (count($volunteers) === 0): ?>
        <tr><td colspan="8" style="text-align:center;color:#aaa;padding:30px;">No volunteer applications found.</td></tr>
      <?php endif; ?>
      <?php foreach ($volunteers as $v): ?>
        <tr>
          <td>
            <?php echo htmlspecialchars($v["fullName"]); ?>
            <?php if (!empty($v["message"])): ?>
              <br><small style="color:#aaa;">"<?php echo htmlspecialchars(mb_strimwidth($v["message"], 0, 70, "...")); ?>"</small>
            <?php endif; ?>
          </td>
          <td><?php echo htmlspecialchars($v["email"]); ?></td>
          <td><?php echo htmlspecialchars($v["phone"]); ?></td>
          <td><?php echo htmlspecialchars($skillLabels[$v["skill"]] ?? $v["skill"]); ?></td>
          <td><?php echo htmlspecialchars($eventLabels[$v["eventChoice"]] ?? $v["eventChoice"]); ?></td>
          <td><?php echo htmlspecialchars(date("d M Y", strtotime($v["submittedAt"]))); ?></td>
          <td><span class="vol-badge vol-<?php echo htmlspecialchars($v["status"]); ?>"><?php echo ucfirst(htmlspecialchars($v["status"])); ?></span></td>
          <td>
            <form method="post" action="admin.php" style="display:inline;" <?php echo "onsubmit=\"return confirm('Are you sure?');\""; ?>>
              <input type="hidden" name="csrf" value="<?php echo htmlspecialchars($_SESSION["csrf"]); ?>">
              <input type="hidden" name="volunteerID" value="<?php echo (int)$v["volunteerID"]; ?>">
              <?php if ($v["status"] !== "approved"): ?>
                <button type="submit" name="action" value="approve" class="vol-btn vol-btn-ok">Approve</button>
              <?php endif; ?>
              <?php if ($v["status"] !== "declined"): ?>
                <button type="submit" name="action" value="decline" class="vol-btn vol-btn-warn">Decline</button>
              <?php endif; ?>
              <button type="submit" name="action" value="delete" class="vol-btn vol-btn-del">Delete</button>
            </form>
          </td>
        </tr>
      <?php endforeach; ?>
      </tbody>
    </table>
    </div>
  </div>

  <!-- REPORTS MANAGEMENT -->
  <div id="reports-manage" class="admin-panel-tab" style="display:none;">
    <div class="admin-panel-header">
      <h2>Manage Reports</h2>
      <button onclick="toggleForm('report-upload-form')" class="add-btn"><i class="fa-solid fa-upload"></i> Upload Report</button>
    </div>
    <div id="report-upload-form" class="admin-form-card" style="display:none;">
      <h3>Upload New Report</h3>
      <input type="text" id="report-title" placeholder="Report Title (e.g. Annual Report 2025)" />
      <select id="report-category">
        <option value="">-- Select Category --</option>
        <option value="annual">Annual Report</option>
        <option value="financial">Financial Report</option>
        <option value="impact">Impact Report</option>
        <option value="strategic">Strategic Plan</option>
      </select>
      <input type="file" id="report-file" accept=".pdf" style="color:white;padding:8px 0;" />
      <button onclick="addReport()" class="save-btn"><i class="fa-solid fa-floppy-disk"></i> Save Report</button>
    </div>
    <table class="admin-table">
      <thead><tr><th>Title</th><th>Category</th><th>Uploaded</th><th>Actions</th></tr></thead>
      <tbody id="reports-table-body">
        <tr><td>Annual Report 2024</td><td>Annual</td><td>1 Jan 2025</td><td><button class="delete-btn" onclick="deleteRow(this)"><i class="fa-solid fa-trash"></i> Delete</button></td></tr>
        <tr><td>Community Impact Report</td><td>Impact</td><td>1 Feb 2025</td><td><button class="delete-btn" onclick="deleteRow(this)"><i class="fa-solid fa-trash"></i> Delete</button></td></tr>
        <tr><td>Financial Transparency Report</td><td>Financial</td><td>1 Mar 2025</td><td><button class="delete-btn" onclick="deleteRow(this)"><i class="fa-solid fa-trash"></i> Delete</button></td></tr>
      </tbody>
    </table>
  </div>

</section>
</div>

<footer class="footer">
<div class="footer-container">
  <div class="footer-column">
    <h3>Tshwane Leadership Foundation</h3>
    <p>Empowering communities through leadership development, education, and social impact initiatives across Tshwane.</p>
  </div>
  <div class="footer-column">
    <h3>Quick Links</h3>
    <ul>
      <li><a href="index.html">Home</a></li>
      <li><a href="about.html">About</a></li>
      <li><a href="news.html">News & Events</a></li>
      <li><a href="reports.html">Reports</a></li>
      <li><a href="contact.html">Get Involved</a></li>
    </ul>
  </div>
  <div class="footer-column">
    <h3>Contact</h3>
    <p><i class="fa-solid fa-phone"></i> +27 12 345 6789</p>
    <p><i class="fa-solid fa-envelope"></i> info@tlfoundation.org</p>
    <p><i class="fa-solid fa-location-dot"></i> Tshwane, South Africa</p>
  </div>
  <div class="footer-column">
    <h3>Follow Us</h3>
    <div class="footer-social">
      <a href="#"><i class="fa-brands fa-facebook"></i></a>
      <a href="#"><i class="fa-brands fa-instagram"></i></a>
      <a href="#"><i class="fa-brands fa-twitter"></i></a>
    </div>
  </div>
</div>
<div class="footer-bottom">
  <p>© 2026 Tshwane Leadership Foundation | Designed by <span class="designer">Ofentse Nyiko Mashigoane</span></p>
</div>
</footer>

<script>
function toggleMenu() {
  document.getElementById('hamburger').classList.toggle('open');
  document.getElementById('mobile-nav').classList.toggle('open');
}
function openAdminTab(id, btn) {
  document.querySelectorAll('.admin-panel-tab').forEach(t => t.style.display = 'none');
  document.querySelectorAll('.admin-tab').forEach(t => t.classList.remove('active'));
  document.getElementById(id).style.display = 'block';
  btn.classList.add('active');
}
function toggleForm(id) {
  const f = document.getElementById(id);
  f.style.display = f.style.display === 'none' ? 'block' : 'none';
}
function deleteRow(btn) {
  if (confirm('Delete this record?')) {
    btn.closest('tr').remove();
  }
}
function addNews() {
  const title = document.getElementById('news-title').value.trim();
  const date  = document.getElementById('news-date').value;
  if (!title || !date) { alert('Please fill in title and date.'); return; }
  const tbody = document.getElementById('news-table-body');
  const tr = document.createElement('tr');
  tr.innerHTML = `<td>${title}</td><td>${date}</td><td><button class="edit-btn"><i class="fa-solid fa-pen"></i> Edit</button> <button class="delete-btn" onclick="deleteRow(this)"><i class="fa-solid fa-trash"></i> Delete</button></td>`;
  tbody.appendChild(tr);
  document.getElementById('news-title').value = '';
  document.getElementById('news-body').value = '';
  document.getElementById('news-date').value = '';
  document.getElementById('news-form').style.display = 'none';
  document.getElementById('stat-news').textContent = tbody.querySelectorAll('tr').length;
}
function addEvent() {
  const title = document.getElementById('event-title').value.trim();
  const date  = document.getElementById('event-date').value;
  const loc   = document.getElementById('event-location').value.trim();
  if (!title || !date || !loc) { alert('Please fill in all event fields.'); return; }
  const tbody = document.getElementById('events-table-body');
  const tr = document.createElement('tr');
  tr.innerHTML = `<td>${title}</td><td>${date}</td><td>${loc}</td><td><button class="edit-btn"><i class="fa-solid fa-pen"></i> Edit</button> <button class="delete-btn" onclick="deleteRow(this)"><i class="fa-solid fa-trash"></i> Delete</button></td>`;
  tbody.appendChild(tr);
  document.getElementById('event-title').value = '';
  document.getElementById('event-desc').value = '';
  document.getElementById('event-date').value = '';
  document.getElementById('event-location').value = '';
  document.getElementById('event-form').style.display = 'none';
  document.getElementById('stat-events').textContent = tbody.querySelectorAll('tr').length;
}
function addReport() {
  const title = document.getElementById('report-title').value.trim();
  const cat   = document.getElementById('report-category').value;
  if (!title || !cat) { alert('Please fill in title and category.'); return; }
  const tbody = document.getElementById('reports-table-body');
  const today = new Date().toLocaleDateString('en-ZA');
  const tr = document.createElement('tr');
  tr.innerHTML = `<td>${title}</td><td>${cat.charAt(0).toUpperCase()+cat.slice(1)}</td><td>${today}</td><td><button class="delete-btn" onclick="deleteRow(this)"><i class="fa-solid fa-trash"></i> Delete</button></td>`;
  tbody.appendChild(tr);
  document.getElementById('report-title').value = '';
  document.getElementById('report-category').value = '';
  document.getElementById('report-file').value = '';
  document.getElementById('report-upload-form').style.display = 'none';
}

// After approving/declining/searching, stay on the Volunteers tab
(function () {
  const tab = new URLSearchParams(window.location.search).get('tab');
  if (tab === 'volunteers') {
    openAdminTab('volunteers-manage', document.querySelector('.admin-tab[onclick*="volunteers-manage"]'));
  }
})();
</script>
</body>
</html>
