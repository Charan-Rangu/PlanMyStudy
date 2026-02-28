const API_BASE_URL = 'http://localhost:8082/api';

document.getElementById('planForm').addEventListener('submit', async (e) => {
    e.preventDefault();

    const submitBtn = document.getElementById('submitBtn');
    const btnText = document.getElementById('btnText');
    const btnLoader = document.getElementById('btnLoader');
    const status = document.getElementById('status');

    const subject = document.getElementById('subject').value.trim();
    const topic = document.getElementById('topic').value.trim();
    const dailyTimeLimit = parseFloat(document.getElementById('dailyTimeLimit').value);
    const deadline = document.getElementById('deadline').value;

    // Validate deadline is in the future
    const deadlineDate = new Date(deadline);
    const today = new Date();
    today.setHours(0, 0, 0, 0);

    if (deadlineDate <= today) {
        status.textContent = 'Deadline must be in the future.';
        status.className = 'status error';
        return;
    }

    if (!topic) {
        status.textContent = 'Please enter a topic.';
        status.className = 'status error';
        return;
    }

    if (dailyTimeLimit <= 0) {
        status.textContent = 'Daily time limit must be greater than 0.';
        status.className = 'status error';
        return;
    }

    // Disable button and show loading
    submitBtn.disabled = true;
    btnText.style.display = 'none';
    btnLoader.style.display = 'inline-block';
    status.textContent = 'Generating your study plan with AI... This may take a moment.';
    status.className = 'status info';

    try {
        const response = await fetch(`${API_BASE_URL}/plan`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({
                subject: subject || null,
                topic: topic,
                dailyTimeLimit: dailyTimeLimit,
                deadline: deadline
            })
        });

        const data = await response.json();

        if (!response.ok) {
            throw new Error(data.error || 'Failed to create study plan');
        }

        // Store study plan ID in sessionStorage and redirect
        sessionStorage.setItem('studyPlanId', data.id);
        window.location.href = 'subtopics.html';

    } catch (error) {
        console.error('Error:', error);
        status.textContent = `Error: ${error.message}`;
        status.className = 'status error';
    } finally {
        submitBtn.disabled = false;
        btnText.style.display = 'inline';
        btnLoader.style.display = 'none';
    }
});

// Set minimum date to today
document.getElementById('deadline').min = new Date().toISOString().split('T')[0];
