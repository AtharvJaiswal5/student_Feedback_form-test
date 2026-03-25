// script.js - Form Validation

function validateForm() {
    let valid = true;

    // Clear previous errors
    document.querySelectorAll('.error').forEach(e => e.textContent = '');

    // 1. Check Student Name
    const name = document.getElementById('name').value.trim();
    if (name === '') {
        document.getElementById('nameError').textContent = 'Name is required.';
        valid = false;
    }

    // 2. Check Email
    const email = document.getElementById('email').value.trim();
    const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    if (!emailRegex.test(email)) {
        document.getElementById('emailError').textContent = 'Enter a valid email.';
        valid = false;
    }

    // 3. Check Mobile Number
    const mobile = document.getElementById('mobile').value.trim();
    const mobileRegex = /^[0-9]{10}$/;
    if (!mobileRegex.test(mobile)) {
        document.getElementById('mobileError').textContent =
            'Enter a valid 10-digit number.';
        valid = false;
    }

    // 4. Check Department
    const dept = document.getElementById('department').value;
    if (dept === '') {
        document.getElementById('deptError').textContent = 'Please select a department.';
        valid = false;
    }

    // 5. Check Gender
    const gender = document.querySelector('input[name="gender"]:checked');
    if (!gender) {
        document.getElementById('genderError').textContent = 'Please select a gender.';
        valid = false;
    }

    // 6. Check Feedback (minimum 10 words)
    const feedback = document.getElementById('feedback').value.trim();
    const wordCount = feedback.split(/\s+/).filter(w => w.length > 0).length;
    if (wordCount < 10) {
        document.getElementById('feedbackError').textContent =
            'Feedback must be at least 10 words.';
        valid = false;
    }

    if (valid) {
        alert('Form submitted successfully!');
        document.getElementById('feedbackForm').reset();
    }
    return false; // prevent actual page reload
}
