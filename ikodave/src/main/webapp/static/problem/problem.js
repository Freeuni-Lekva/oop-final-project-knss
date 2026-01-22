document.addEventListener('DOMContentLoaded', () => {
    loadHeaders().catch(console.error);
    loadProblem().catch(console.error);
});

async function loadHeaders() {
    const data = await fetch('/api/user/session');
    const session = await data.json();

    if (!session.loggedIn || !session.verified) {
        return;
    }

    document.getElementById('header').innerHTML = `
        <button type="button" id="submissionsButton" class="btn btn-link mb-3">
            My Submissions
        </button>

        <button type="button" id="submitSolutionButton" class="btn btn-link mb-3">
            Submit Solution
        </button>
    `;

    document.getElementById('submissionsButton').onclick = () => {
        const title = getProblemTitleFromPath();
        window.location.href = `/problems/submissions/${encodeURIComponent(title)}`;
    };
    document.getElementById('submitSolutionButton').onclick = () => {
        const title = getProblemTitleFromPath();
        window.location.href = `/problems/submit/${encodeURIComponent(title)}`;
    };
}


async function loadProblem() {
    const title = getProblemTitleFromPath();
    const data = await fetch(`/api/problems/problem`, {
        method: 'POST',
        headers: {'Content-Type': 'application/json'},
        body: JSON.stringify({ problemTitle: title })
    }).then(r => r.json());

    document.getElementById('problemTitle').textContent = title;

    document.getElementById('descriptionText').textContent = data.problemDescription;

    const statusTextElem = document.getElementById('statusText');
    const statusValue = data.problemStatus.toLowerCase();
    statusTextElem.textContent = data.problemStatus;
    const statusClass = ['attempted', 'accepted', 'todo', 'solved'].includes(statusValue)
        ? statusValue
        : 'todo';
    statusTextElem.className = 'status ' + statusClass;

    const diffTextElem = document.getElementById('difficultyText');
    const diffValue = data.problemDifficulty.toLowerCase();
    diffTextElem.textContent = data.problemDifficulty;
    const diffClass = ['easy', 'medium', 'hard'].includes(diffValue)
        ? diffValue
        : 'easy';
    diffTextElem.className = 'difficulty ' + diffClass;

    document.getElementById('inputText').textContent  = data.problemInputSpec;
    document.getElementById('outputText').textContent = data.problemOutputSpec;

    document.getElementById('problemLimits').innerHTML =
        `Time limit per test: <strong>${data.problemTime} ms</strong>.`;

    const testCasesContainer = document.getElementById('testCases');
    testCasesContainer.innerHTML = '';
    (data.problemTestCases || []).forEach(tc => {
        const ex = document.createElement('div');
        ex.className = 'example';
        ex.innerHTML = `
            <div><strong>Input:</strong></div>
            <pre>${tc.problemInput}</pre>
            <div><strong>Output:</strong></div>
            <pre>${tc.problemOutput}</pre>
        `;
        testCasesContainer.appendChild(ex);
    });

    const topicsContainer = document.getElementById('problemTopics');
    topicsContainer.innerHTML = '';
    (data.problemTopics || []).forEach(topic => {
        const tag = document.createElement('span');
        tag.className = 'tag';
        tag.textContent = topic;
        topicsContainer.appendChild(tag);
    });
}

function getProblemTitleFromPath() {
    return window.location.pathname.split('/').filter(Boolean).pop();
}
