document.addEventListener('DOMContentLoaded', () => {
    const form = document.getElementById("registration-form");
    const errorMsg = document.getElementById("error-msg");

    form.addEventListener("submit", async (e) => {
        e.preventDefault();
        const mail = document.getElementById('mail').value.trim();
        const username = document.getElementById("username").value.trim();
        const password = document.getElementById("password").value.trim();
        const confirmPassword = document.getElementById("confirm-password").value.trim();

        try {
            const res = await fetch("/registration", {
                method: "POST",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify(
                    {
                        mail,
                        username,
                        password,
                        confirmPassword
                    }),
            });
            const data = await res.json();

            if (data.status === "exists") {
                errorMsg.innerHTML = '<div class="alert alert-danger text-center">Mail or Username is already taken.</div>';
            }
            else if (data.status === 'invalid-password') {
                errorMsg.innerHTML = '<div class="alert alert-danger text-center">Password is not valid.</div>';
            }
            else if (data.status === 'invalid-confirm-password') {
                errorMsg.innerHTML = '<div class="alert alert-danger text-center">Confirm Password is not valid.</div>';
            }
            else if (data.status === "ok") {
                window.location.href = "/profile";
            }
        } catch (err) {
            console.error("Registration error:", err);
            errorMsg.innerHTML =
                '<div class="alert alert-danger text-center">Something went wrong. Try again.</div>';
        }
    });
});
