document.addEventListener('DOMContentLoaded', (e) => {
    addButtonListeners();
    loadUserSubmissions();
    registerSubmissionsAsyncListeners();
});

function addButtonListeners() {
    document.getElementById('backToProblemButton').onclick = backToProblemButton;
}

function backToProblemButton() {
    const parts = window.location.pathname.split('/').filter(Boolean);
    const title = parts[parts.length - 1];
    window.location.href = `/problems/${encodeURIComponent(title)}`;
}

function loadUserSubmissions() {
    const parts = window.location.pathname.split('/');
    const problemTitle = parts[parts.length - 1];

    fetch(`/api/problems/submissions/${problemTitle}`)
        .then(res => res.json())
        .then(data => {
            const container = document.getElementById('submissions');
            container.innerHTML = '';

            if (!data || typeof data !== 'object' || !('submissions' in data)) {
                container.innerHTML = `<a href="/signin">Sign in to view Submissions</a>`;
                return;
            }

            const submissions = data.submissions;

            if (!submissions.length) {
                container.innerHTML = '<p>No submissions yet.</p>';
                return;
            }

            submissions.forEach((sub, index) => {
                const card = document.createElement('div');
                card.className = 'card mb-3';
                card.style.padding = '10px';

                // Verdict header with color coding
                const header = document.createElement('h5');
                header.textContent = `#${submissions.length - index} — ${sub.verdict}`;
                const status = sub.verdict.toLowerCase();
                header.classList.add('mb-2');
                if (status === 'accepted') {
                    header.classList.add('text-success');
                } else if (status === 'running') {
                    header.classList.add('text-primary');
                } else {
                    header.classList.add('text-danger');
                }
                card.appendChild(header);

                // Format submission date and time
                let formattedDateTime = sub.submitDate;
                try {
                    const dt = new Date(sub.submitDate);
                    const pad = n => String(n).padStart(2, '0');
                    const yyyy = dt.getFullYear();
                    const mm = pad(dt.getMonth() + 1);
                    const dd = pad(dt.getDate());
                    const hh = pad(dt.getHours());
                    const min = pad(dt.getMinutes());
                    const ss = pad(dt.getSeconds());
                    formattedDateTime = `${yyyy}-${mm}-${dd} ${hh}:${min}:${ss}`;
                } catch (e) {
                }

                const details = document.createElement('p');
                details.innerHTML = `
                    <strong>User:</strong>
                    <a href="/profile/${encodeURIComponent(sub.username)}">${sub.username}</a>
                    &nbsp;|&nbsp;
                    <strong>Language:</strong> ${sub.codeLanguage}
                    &nbsp;|&nbsp;
                    <strong>Time:</strong> ${sub.time} ms
                    &nbsp;|&nbsp;
                    <strong>Date:</strong> ${formattedDateTime}
                `;
                card.appendChild(details);

                const toggleCode = document.createElement('button');
                toggleCode.textContent = 'Show Code';
                toggleCode.className = 'btn btn-sm btn-outline-secondary me-2';
                card.appendChild(toggleCode);

                const codePre = document.createElement('pre');
                codePre.style.display = 'none';
                codePre.style.background = '#f0f0f0';
                codePre.style.padding = '10px';
                codePre.textContent = sub.solutionCode;
                card.appendChild(codePre);

                toggleCode.addEventListener('click', () => {
                    const isHidden = codePre.style.display === 'none';
                    codePre.style.display = isHidden ? 'block' : 'none';
                    toggleCode.textContent = isHidden ? 'Hide Code' : 'Show Code';
                });

                // Show Logs toggle
                const toggleLog = document.createElement('button');
                toggleLog.textContent = 'Show Logs';
                toggleLog.className = 'btn btn-sm btn-outline-secondary mb-2';
                card.appendChild(toggleLog);

                const logPre = document.createElement('pre');
                logPre.style.display = 'none';
                logPre.style.background = '#f9f9f9';
                logPre.style.padding = '10px';
                logPre.style.marginTop = '5px';
                logPre.textContent = sub.log || 'No logs available.';
                card.appendChild(logPre);

                toggleLog.addEventListener('click', () => {
                    const isHidden = logPre.style.display === 'none';
                    logPre.style.display = isHidden ? 'block' : 'none';
                    toggleLog.textContent = isHidden ? 'Hide Logs' : 'Show Logs';
                });

                container.appendChild(card);
            });
        })
        .catch((error) => {
            console.error(error);
            document.getElementById('submissions').textContent = 'Error loading submissions.';
        });
}
