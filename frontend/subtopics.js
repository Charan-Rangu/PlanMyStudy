const API_BASE_URL = 'http://localhost:8082/api';

async function loadSubtopics() {
    const studyPlanId = sessionStorage.getItem('studyPlanId');

    if (!studyPlanId) {
        document.getElementById('subtopicsList').innerHTML =
            '<div class="status error">No study plan found. Please create a new plan.</div>';
        return;
    }

    try {
        // Load study plan info
        console.log(`Loading plan ID: ${studyPlanId}`);
        const planResponse = await fetch(`${API_BASE_URL}/plan/${studyPlanId}`);
        console.log(`Plan response status: ${planResponse.status}`);
        if (!planResponse.ok) {
            throw new Error(`Failed to load study plan: ${planResponse.status} ${planResponse.statusText}`);
        }
        const studyPlan = await planResponse.json();

        // Display plan info
        const planInfo = document.getElementById('planInfo');
        const daysUntilDeadline = Math.ceil(
            (new Date(studyPlan.deadline) - new Date()) / (1000 * 60 * 60 * 24)
        );

        planInfo.innerHTML = `
            <div class="plan-info-item"><strong>Subject:</strong> ${studyPlan.subject || 'Not specified'}</div>
            <div class="plan-info-item"><strong>Topic:</strong> ${studyPlan.topic}</div>
            <div class="plan-info-item"><strong>Daily Time Limit:</strong> ${studyPlan.dailyTimeLimit} hours</div>
            <div class="plan-info-item"><strong>Deadline:</strong> ${new Date(studyPlan.deadline).toLocaleDateString()}</div>
            <div class="plan-info-item"><strong>Days Remaining:</strong> ${daysUntilDeadline} days</div>
        `;

        // Load subtopics
        console.log(`Loading subtopics for plan ID: ${studyPlanId}`);
        const subtopicsResponse = await fetch(`${API_BASE_URL}/plan/${studyPlanId}/subtopics`);
        console.log(`Subtopics response status: ${subtopicsResponse.status}`);
        if (!subtopicsResponse.ok) {
            throw new Error(`Failed to load subtopics: ${subtopicsResponse.status} ${subtopicsResponse.statusText}`);
        }
        const subtopics = await subtopicsResponse.json();

        if (subtopics.length === 0) {
            document.getElementById('subtopicsList').innerHTML =
                '<div class="status info">No subtopics found for this study plan.</div>';
            return;
        }

        // Group subtopics by day
        const groupedSubtopics = subtopics.reduce((groups, subtopic) => {
            const day = subtopic.dayNumber || 1;
            if (!groups[day]) groups[day] = [];
            groups[day].push(subtopic);
            return groups;
        }, {});

        // Display subtopics
        const subtopicsList = document.getElementById('subtopicsList');
        subtopicsList.innerHTML = '';

        Object.keys(groupedSubtopics).sort((a, b) => a - b).forEach(day => {
            const daySubtopics = groupedSubtopics[day];
            const dayTotalHours = daySubtopics.reduce((sum, s) => sum + s.estimatedHours, 0);

            const daySection = document.createElement('div');
            daySection.className = 'day-group';
            daySection.innerHTML = `
                <div class="day-header">
                    <h3>Day ${day}</h3>
                    <span class="day-total">Total: ${dayTotalHours.toFixed(1)} hours</span>
                </div>
                <div class="day-cards">
                    ${daySubtopics.map((subtopic) => `
                        <div class="subtopic-card ${subtopic.completed ? 'completed' : ''}" id="card-${subtopic.id}" onclick="viewSubtopic('${subtopic.id}', '${studyPlanId}')">
                            <div class="completion-toggle" onclick="toggleCompletion('${subtopic.id}', event)">
                                ${subtopic.completed ? '✅' : '⭕'}
                            </div>
                            <div class="subtopic-info">
                                <div class="subtopic-name">${subtopic.name}</div>
                                <div class="subtopic-hours">⏱️ ${subtopic.estimatedHours.toFixed(1)} hours</div>
                            </div>
                            <div class="subtopic-arrow">→</div>
                        </div>
                    `).join('')}
                </div>
            `;
            subtopicsList.appendChild(daySection);
        });

    } catch (error) {
        console.error('Error:', error);
        document.getElementById('subtopicsList').innerHTML =
            `<div class="status error">Error loading subtopics: ${error.message}</div>`;
    }
}

async function toggleCompletion(subtopicId, event) {
    event.stopPropagation(); // Prevent card click (navigation)

    try {
        const response = await fetch(`${API_BASE_URL}/subtopic/${subtopicId}/toggle`, {
            method: 'PATCH'
        });

        if (!response.ok) {
            throw new Error('Failed to toggle completion');
        }

        const updatedSubtopic = await response.json();

        // Update UI
        const card = document.getElementById(`card-${subtopicId}`);
        const toggle = card.querySelector('.completion-toggle');

        if (updatedSubtopic.completed) {
            card.classList.add('completed');
            toggle.textContent = '✅';
        } else {
            card.classList.remove('completed');
            toggle.textContent = '⭕';
        }

    } catch (error) {
        console.error('Error toggling completion:', error);
        alert('Failed to update completion status. Please try again.');
    }
}

function viewSubtopic(subtopicId, studyPlanId) {
    sessionStorage.setItem('subtopicId', subtopicId);
    sessionStorage.setItem('studyPlanId', studyPlanId);
    window.location.href = 'content.html';
}

// Load subtopics when page loads
loadSubtopics();
