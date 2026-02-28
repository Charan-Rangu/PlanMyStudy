const API_BASE_URL = 'http://localhost:8082/api';

function goBack() {
    window.location.href = 'subtopics.html';
}

async function loadContent() {
    const subtopicId = sessionStorage.getItem('subtopicId');
    const studyPlanId = sessionStorage.getItem('studyPlanId');

    if (!subtopicId || !studyPlanId) {
        document.getElementById('contentContainer').innerHTML =
            '<div class="status error">No subtopic selected. Please go back and select a subtopic.</div>';
        return;
    }

    try {
        const response = await fetch(`${API_BASE_URL}/plan/${studyPlanId}/subtopic/${subtopicId}`);

        if (!response.ok) {
            throw new Error('Failed to load subtopic content');
        }

        const subtopic = await response.json();

        const contentContainer = document.getElementById('contentContainer');

        contentContainer.innerHTML = `
            <div class="content-header">
                <h2>${subtopic.name}</h2>
                <div class="subtopic-hours">⏱️ ${subtopic.estimatedHours.toFixed(1)} hours</div>
            </div>

            <div class="content-section">
                <h3>Study Content</h3>
                <div class="content-text">${subtopic.content || 'Content is being generated...'}</div>
            </div>

            ${subtopic.youtubeLinks && subtopic.youtubeLinks.length > 0 ? `
                <div class="videos-section">
                    <h3>📺 Recommended Videos</h3>
                    <div class="videos-list">
                        ${subtopic.youtubeLinks.map((link, index) => `
                            <a href="${link}" target="_blank" class="video-link">
                                <div class="video-icon">▶️</div>
                                <div class="video-text">
                                    <div class="video-label">Video ${index + 1}</div>
                                    <div class="video-url">${link}</div>
                                </div>
                            </a>
                        `).join('')}
                    </div>
                </div>
            ` : '<div class="status info">Direct video links are not available for this subtopic.</div>'}

            <div class="videos-section fallback-section">
                <h3>🔍 Find More on YouTube</h3>
                <p class="search-hint">If the videos above are unavailable, try these search results:</p>
                <div class="videos-list">
                    ${(subtopic.videoSearchQueries && subtopic.videoSearchQueries.length > 0)
                ? subtopic.videoSearchQueries.map((query, index) => `
                            <a href="https://www.youtube.com/results?search_query=${encodeURIComponent(query)}" target="_blank" class="video-link search-link highlighted">
                                <div class="video-icon">🔍</div>
                                <div class="video-text">
                                    <div class="video-label">Search Result ${index + 1}</div>
                                    <div class="video-url">"${query}"</div>
                                </div>
                            </a>
                            <a href="https://www.youtube.com/results?search_query=${encodeURIComponent(query)}&sp=CAI%253D" target="_blank" class="video-link search-link highlighted recent-link">
                                <div class="video-icon">🕒</div>
                                <div class="video-text">
                                    <div class="video-label">Recent Search ${index + 1}</div>
                                    <div class="video-url">"${query}" (Sort by Date)</div>
                                </div>
                            </a>
                        `).join('')
                : `
                            <a href="https://www.youtube.com/results?search_query=${encodeURIComponent(subtopic.name)}" target="_blank" class="video-link search-link highlighted">
                                <div class="video-icon">🔍</div>
                                <div class="video-text">
                                    <div class="video-label">Universal Search</div>
                                    <div class="video-url">Search for "${subtopic.name}"</div>
                                </div>
                            </a>
                            <a href="https://www.youtube.com/results?search_query=${encodeURIComponent(subtopic.name)}&sp=CAI%253D" target="_blank" class="video-link search-link highlighted recent-link">
                                <div class="video-icon">🕒</div>
                                <div class="video-text">
                                    <div class="video-label">Universal Recent Search</div>
                                    <div class="video-url">Search for "${subtopic.name}" (Sort by Date)</div>
                                </div>
                            </a>
                        `
            }
                </div>
            </div>
        `;

    } catch (error) {
        console.error('Error:', error);
        document.getElementById('contentContainer').innerHTML =
            `<div class="status error">Error loading content: ${error.message}</div>`;
    }
}

// Load content when page loads
loadContent();
