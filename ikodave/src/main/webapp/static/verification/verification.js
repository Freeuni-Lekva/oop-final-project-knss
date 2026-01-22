let secondsLeft = 0;
let timerInterval = null;

function fetchTimeLeft() {
    fetch('/api/verification/time')
        .then(response => {
            if (!response.ok) throw new Error('Failed to fetch time left');
            return response.json();
        })
        .then(data => {
            const expiry = new Date(data.expiry);
            const now = new Date();
            secondsLeft = Math.floor((expiry - now) / 1000);
            updateTimer();
            if (timerInterval) clearInterval(timerInterval);
            timerInterval = setInterval(tick, 1000);
        })
        .catch(err => {
            document.getElementById('timer').textContent = '';
        });
}

function tick() {
    secondsLeft--;
    updateTimer();
    if (secondsLeft <= 0) {
        clearInterval(timerInterval);
    }
}

function updateTimer() {
    if (secondsLeft <= 0) {
        document.getElementById('timer').textContent = 'Verification expired. Please resend verification code again.';
        clearInterval(timerInterval);
        return;
    }
    const minutes = Math.floor(secondsLeft / 60);
    const seconds = secondsLeft % 60;
    document.getElementById('timer').textContent = `Time left: ${minutes}:${seconds.toString().padStart(2, '0')}`;
}

document.getElementById('resend-btn').addEventListener('click', function() {
    fetch('/verification/resend', { method: 'POST' })
        .then(response => {
            if (!response.ok) throw new Error('Failed to resend email');
            return response.json();
        })
        .then(data => {
            alert('Verification email resent')
            fetchTimeLeft();
        })
        .catch(err => {
            alert('Failed to resend verification email');
        });
});

fetchTimeLeft();