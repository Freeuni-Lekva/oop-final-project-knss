document.addEventListener('DOMContentLoaded', () => {
    const form = document.getElementById("signin-form");
    const errorMsg = document.getElementById("error-msg");

    form.addEventListener("submit", async (e) => {
        e.preventDefault();
        const username = document.getElementById("username").value.trim();
        const password = document.getElementById("password").value.trim();

        try {
            const res = await fetch("/signin", {
                method: "POST",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify({ username, password }),
            });
            const data = await res.json();

            if (data.status === "invalid") {
                errorMsg.innerHTML = '<div class="alert alert-danger text-center">Username or password is incorrect.</div>';
            } else if (data.status === "ok") {
                window.location.href = "/profile";
            }
        } catch (err) {
            console.error("Sign-in error:", err);
            errorMsg.innerHTML =
                '<div class="alert alert-danger text-center">Something went wrong. Try again.</div>';
        }
    });
});

function handleGoogleCredential(response) {
    const idToken = response.credential;

    fetch('/api/outh2', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ id_token: idToken })
    })
        .then(res => {
            if (!res.ok) throw new Error('Invalid ID token');
            return res.json();
        })
        .then(profile => {
            if (profile.auth) {
                window.location.href = '/profile';
            }
            else {
                window.location.href = '/registration';
            }
        })
        .catch(err => {
            document.getElementById('error-msg').innerText =
                'Google sign-in error: ' + err.message;
        });
}
