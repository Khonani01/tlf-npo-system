// =============================================
//   TLF COMPANION APPLICATION - script.js
// =============================================


// ── JOIN US BUTTON ──────────────────────────
const joinBtn = document.querySelector('.join');
if (joinBtn) {
  joinBtn.addEventListener('click', function () {
    window.location.href = 'contact.html';
  });
}


// (The homepage admin login is now a real form that posts to process_login.php.
//  The old hardcoded username/password check was removed for security.)


// ── SIGNUP PAGE ─────────────────────────────

// Helper: show popup
function showPopup(success, title, message, redirectUrl) {
  const overlay  = document.getElementById('popup-overlay');
  const box      = document.getElementById('popup-box');
  const icon     = document.getElementById('popup-icon');
  const titleEl  = document.getElementById('popup-title');
  const msgEl    = document.getElementById('popup-message');
  const closeBtn = document.getElementById('popup-close-btn');

  // Set content
  icon.textContent    = success ? '✅' : '❌';
  titleEl.textContent = title;
  msgEl.textContent   = message;

  // Set style
  box.classList.toggle('error', !success);

  // Show overlay
  overlay.classList.add('active');

  // Close button behaviour
  closeBtn.onclick = function () {
    overlay.classList.remove('active');
    if (success && redirectUrl) {
      window.location.href = redirectUrl;
    }
  };
}

const roleSelect = document.getElementById('role');
if (roleSelect) {

  // Show/hide fields based on role
  roleSelect.addEventListener('change', function () {
    const role = this.value;

    document.getElementById('common-fields').style.display        = role ? 'block' : 'none';
    document.getElementById('common-fields-bottom').style.display = role ? 'block' : 'none';

    document.querySelectorAll('.role-fields').forEach(function (el) {
      el.classList.remove('active');
    });

    if (role) {
      document.getElementById(role + '-fields').classList.add('active');
    }
  });

  // Sign up button
  const signupBtn = document.getElementById('signup-btn');
  if (signupBtn) {
    signupBtn.addEventListener('click', function () {

      const role      = document.getElementById('role').value;
      const firstname = document.getElementById('firstname').value.trim();
      const lastname  = document.getElementById('lastname').value.trim();
      const dob       = document.getElementById('dob').value;
      const email     = document.getElementById('email').value.trim();
      const phone     = document.getElementById('phone').value.trim();
      const password  = document.getElementById('signup-password').value.trim();
      const confirm   = document.getElementById('confirm-password').value.trim();

      // Validate required common fields
      if (!role) {
        showPopup(false, 'Missing Role', 'Please select whether you are signing up as a Volunteer, Stakeholder, or Donor.');
        return;
      }
      if (!firstname || !lastname) {
        showPopup(false, 'Missing Name', 'Please enter both your first and last name.');
        return;
      }
      if (!dob) {
        showPopup(false, 'Missing Date of Birth', 'Please enter your date of birth.');
        return;
      }
      if (!email) {
        showPopup(false, 'Missing Email', 'Please enter your email address.');
        return;
      }
      if (!phone) {
        showPopup(false, 'Missing Phone Number', 'Please enter your phone number.');
        return;
      }
      if (!password) {
        showPopup(false, 'Missing Password', 'Please create a password for your account.');
        return;
      }
      if (password.length < 6) {
        showPopup(false, 'Password Too Short', 'Your password must be at least 6 characters long.');
        return;
      }
      if (password !== confirm) {
        showPopup(false, 'Passwords Do Not Match', 'The passwords you entered do not match. Please try again.');
        return;
      }

      // Validate role-specific required fields
      if (role === 'volunteer') {
        if (!document.getElementById('availability').value) {
          showPopup(false, 'Missing Availability', 'Please select your availability as a volunteer.');
          return;
        }
      }

      if (role === 'stakeholder') {
        if (!document.getElementById('organisation').value.trim()) {
          showPopup(false, 'Missing Organisation', 'Please enter your organisation or company name.');
          return;
        }
      }

      if (role === 'donor') {
        if (!document.getElementById('donation-type').value) {
          showPopup(false, 'Missing Donation Type', 'Please select your preferred donation type.');
          return;
        }
      }

      // All good — show success popup
      // When Firebase is connected, save user data here before showing popup
      signupBtn.disabled = true;

      const roleLabel = role.charAt(0).toUpperCase() + role.slice(1);
      showPopup(
        true,
        'Account Created!',
        'Welcome, ' + firstname + '! You have successfully signed up as a ' + roleLabel + '. You will now be redirected to the homepage.',
        'index.html'
      );

    });
  }
}


// ── NEWS PAGE: READ MORE / READ LESS ────────
const readMoreBtns = document.querySelectorAll('.news-card a');
if (readMoreBtns.length > 0) {

  const extraInfo = [
    'The workshop was attended by over 80 young people from various Tshwane communities. Participants took part in leadership exercises, group discussions, and motivational talks by community leaders. The event was held at the TLF Community Centre on 15 March 2026 and ran over two full days. Certificates were awarded to all participants upon completion.',
    'The community development program focused on upgrading public spaces in three Tshwane townships. Over 120 volunteers contributed their time to cleaning, painting, and planting trees in local parks. The program is part of TLF\'s ongoing commitment to improving the quality of life in underserved areas. The next clean-up drive is scheduled for April 2026.',
    'The Education Support Initiative has so far benefited over 300 learners across 5 schools in Tshwane. The programme provides after-school tutoring, stationery packs, and access to tablets for digital learning. TLF partnered with three corporate sponsors to fund the initiative for the 2026 academic year. Applications for the mentorship programme are now open.'
  ];

  readMoreBtns.forEach(function (btn, index) {
    btn.href = '#';

    const extraP = document.createElement('p');
    extraP.style.display    = 'none';
    extraP.style.marginTop  = '10px';
    extraP.style.fontSize   = '13px';
    extraP.style.lineHeight = '1.7';
    extraP.style.color      = '#ddd';
    extraP.style.padding    = '0 15px 10px';
    extraP.textContent      = extraInfo[index];

    btn.parentNode.insertBefore(extraP, btn);

    btn.addEventListener('click', function (e) {
      e.preventDefault();
      const isExpanded = extraP.style.display === 'block';
      if (isExpanded) {
        extraP.style.display = 'none';
        btn.textContent      = 'Read More';
      } else {
        extraP.style.display = 'block';
        btn.textContent      = 'Read Less';
      }
    });
  });
}