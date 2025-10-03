<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>Participant Management</title>
  <!-- Poppins Font -->
  <link href="https://fonts.googleapis.com/css2?family=Poppins:wght@400;500;600;700&display=swap" rel="stylesheet">
  <style>
    * {
      margin: 0;
      padding: 0;
      box-sizing: border-box;
      font-family: 'Poppins', sans-serif;
    }
    body {
      background: #fff;
      color: #333;
    }
    header {
      display: flex;
      justify-content: space-between;
      align-items: center;
      padding: 15px 20px;
      background: #fff;
      border-bottom: 2px solid #eee;
    }
    header h1 {
      font-size: 20px;
      font-weight: 700;
    }
    .menu-toggle {
      font-size: 24px;
      cursor: pointer;
    }
    /* Sidebar Menu */
    .sidebar {
      position: fixed;
      top: 0;
      left: -260px;
      width: 260px;
      height: 100%;
      background: #fff;
      box-shadow: 2px 0 10px rgba(0,0,0,0.1);
      transition: 0.3s;
      padding: 20px;
      z-index: 1000;
    }
    .sidebar.active {
      left: 0;
    }
    .sidebar h2 {
      margin-bottom: 20px;
      font-weight: 700;
      font-size: 22px;
    }
    .sidebar button {
      display: block;
      width: 100%;
      margin-bottom: 10px;
      padding: 10px;
      border: none;
      background: #f5c400;
      font-weight: 600;
      cursor: pointer;
      text-align: left;
      border-radius: 4px;
    }
    .sidebar button:hover {
      background: #e0b200;
    }
    .sidebar .logout {
      margin-top: 20px;
      background: #333;
      color: #fff;
    }
    .sidebar .logout:hover {
      background: #555;
    }
    main {
      padding: 20px;
    }
    h2.section-title {
      font-size: 24px;
      margin-bottom: 20px;
      font-weight: 700;
    }
    /* Participant Cards */
    .participants {
      display: flex;
      gap: 20px;
      flex-wrap: wrap;
      margin-top: 20px;
    }
    .card {
      background: #f5c400;
      border-radius: 20px;
      width: 200px;
      padding: 20px;
      text-align: center;
      color: #000;
      font-weight: 600;
    }
    .card .photo {
      width: 100px;
      height: 100px;
      border-radius: 50%;
      background: #fff;
      margin: 0 auto 10px;
    }
    .card p {
      margin: 5px 0;
      font-weight: 500;
    }
    /* Add Participant Form */
    #add-form {
      display: none;
      margin: 30px auto;
      border: 2px solid #333;
      border-radius: 8px;
    }
    #add-form h2 {
      background: #f5c400;
      padding: 10px;
      text-align: center;
      font-weight: 700;
      font-size: 20px;
    }
    .form-container {
      display: flex;
      justify-content: space-between;
      padding: 20px;
    }
    .form-left {
      width: 45%;
    }
    .form-right {
      width: 45%;
      text-align: center;
    }
    .form-group {
      margin-bottom: 12px;
    }
    .form-group label {
      display: block;
      margin-bottom: 5px;
      font-weight: 600;
      font-size: 14px;
    }
    .form-group input {
      width: 100%;
      padding: 8px;
      border: 1px solid #aaa;
      border-radius: 4px;
    }
    .upload-photo button {
      margin: 10px 0;
      padding: 8px 15px;
      background: #f5c400;
      border: none;
      cursor: pointer;
      font-weight: 600;
      border-radius: 4px;
    }
    .upload-photo button:hover {
      background: #e0b200;
    }
    .upload-photo .preview {
      width: 200px;
      height: 200px;
      border: 3px solid #f5c400;
      background: #eee;
      margin: 10px auto;
      background-size: cover;
      background-position: center;
    }
    #add-form .submit-btn {
      padding: 10px;
      background: #f5c400;
      border: none;
      cursor: pointer;
      font-weight: 700;
      border-radius: 6px;
      margin-top: 10px;
    }
    #add-form .submit-btn:hover {
      background: #e0b200;
    }
  </style>
</head>
<body>
  <header>
    <span class="menu-toggle">&#9776;</span>
    <h1>UNIVERSITY OF NUEVA CACERES</h1>
  </header>

  <!-- Sidebar -->
  <div class="sidebar" id="sidebar">
    <h2>MENU</h2>
    <button onclick="showSection('participants')">Participant Management</button>
    <button onclick="showSection('add-form')">Add Participant</button>
    <button>Candidate Management</button>
    <button>Judge Management</button>
    <button>Scoring</button>
    <button>Results and Ranking</button>
    <button class="logout">Log Out</button>
  </div>

  <main>
    <!-- Participants Section -->
    <section id="participants">
      <h2 class="section-title">PARTICIPANT MANAGEMENT</h2>
      <button onclick="showSection('add-form')" style="padding:10px 15px;background:#f5c400;border:none;border-radius:4px;font-weight:600;cursor:pointer;">+ Add Participant</button>
      <div class="participants" id="participant-list">
        <div class="card">
          <div class="photo"></div>
          <p>Maria J. Dela Cruz</p>
          <p>Naga City, Camarines Sur</p>
        </div>
        <div class="card">
          <div class="photo"></div>
          <p>Anna B. Castillo</p>
          <p>Legazpi, Albay</p>
        </div>
        <div class="card">
          <div class="photo"></div>
          <p>Rachel Q. Castro</p>
          <p>Matnog, Sorsogon</p>
        </div>
      </div>
    </section>

    <!-- Add Participant Section -->
    <section id="add-form">
      <h2>ADD PARTICIPANT</h2>
      <div class="form-container">
        <div class="form-left">
          <h3 style="margin-bottom:15px;">Personal Information</h3>
          <div class="form-group">
            <label>Name</label>
            <input type="text" id="name">
          </div>
          <div class="form-group">
            <label>Representing</label>
            <input type="text" id="representing">
          </div>
          <div class="form-group">
            <label>Nationality</label>
            <input type="text" id="nationality">
          </div>
          <div class="form-group">
            <label>Address</label>
            <input type="text" id="address">
          </div>
          <div class="form-group">
            <label>Age</label>
            <input type="number" id="age">
          </div>
          <div class="form-group">
            <label>Height</label>
            <input type="text" id="height">
          </div>
          <div class="form-group">
            <label>Sex</label>
            <input type="text" id="sex">
          </div>
          <button class="submit-btn" onclick="addParticipant()">Add Participant</button>
        </div>
        <div class="form-right">
          <div class="upload-photo">
            <div class="preview" id="preview">INSERT PHOTO</div>
            <button onclick="document.getElementById('photo').click()">Upload Photo</button>
            <input type="file" id="photo" accept="image/*" style="display:none" onchange="previewPhoto(event)">
          </div>
        </div>
      </div>
    </section>
  </main>

  <script>
    const sidebar = document.getElementById('sidebar');
    const toggle = document.querySelector('.menu-toggle');
    toggle.addEventListener('click', () => {
      sidebar.classList.toggle('active');
    });

    function showSection(id) {
      document.getElementById('participants').style.display = 'none';
      document.getElementById('add-form').style.display = 'none';
      document.getElementById(id).style.display = 'block';
    }

    function previewPhoto(event) {
      const preview = document.getElementById('preview');
      const file = event.target.files[0];
      if (file) {
        preview.style.backgroundImage = `url(${URL.createObjectURL(file)})`;
        preview.textContent = "";
      }
    }

    function addParticipant() {
      const name = document.getElementById('name').value;
      const representing = document.getElementById('representing').value;
      const nationality = document.getElementById('nationality').value;
      const address = document.getElementById('address').value;
      const age = document.getElementById('age').value;
      const height = document.getElementById('height').value;
      const sex = document.getElementById('sex').value;
      const photo = document.getElementById('preview').style.backgroundImage;

      if (!name || !representing || !age) {
        alert("Please fill in required fields.");
        return;
      }

      const card = document.createElement('div');
      card.classList.add('card');
      card.innerHTML = `
        <div class="photo" style="background-image:${photo}; background-size:cover; background-position:center;"></div>
        <p>${name}</p>
        <p>${representing}</p>
      `;

      document.getElementById('participant-list').appendChild(card);

      // Reset form
      document.getElementById('name').value = "";
      document.getElementById('representing').value = "";
      document.getElementById('nationality').value = "";
      
      document.getElementById('address').value = "";
      document.getElementById('age').value = "";
      document.getElementById('height').value = "";
      document.getElementById('sex').value = "";
      document.getElementById('preview').style.backgroundImage = "";
      document.getElementById('preview').textContent = "INSERT PHOTO";

      showSection('participants');
    }
  </script>
</body>
</html>
