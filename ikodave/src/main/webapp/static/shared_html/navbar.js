loadNavbar().catch(console.error);

async function loadNavbar() {
    await initNavBar();
    await addNavs();
}

async function initNavBar() {
    const navbar = document.getElementById('navbar-container');

    navbar.innerHTML = await fetch('/static/shared_html/navbar.html')
            .then(res => res.text())
            .catch(console.error);
}


async function addNavs() {
    const data = await fetch('/api/user/session')
    const session = await data.json();

    if (!session.loggedIn) {
        addLoggedOutNavs();
    }
    else if (!session.verified) {
        addLoggedInNotVerifiedNavs();
    }
    else if (session.role.toLowerCase() === 'admin') {
        addLoggedInAdminNavs();
    }
    else {
        addLoggedInUserNavs();
    }

}

function addLoggedOutNavs() {
    const navbarList = document.getElementById('navbar-list');

    let homeElem = document.createElement('li');
    homeElem.innerHTML = `<li class="nav-item" id="nav-home"><a class="nav-link" href="/home">Home</a></li>`;

    let registerElem = document.createElement('li');
    registerElem.innerHTML = `<li class="nav-item" id="register"><a class="nav-link" href="/registration">Register</a></li>`;

    let signInElem = document.createElement('li');
    signInElem.innerHTML = `<li class="nav-item" id="signin"><a class="nav-link" href="/signin">Sign in</a></li>`;

    let problemsElem = document.createElement('li');
    problemsElem.innerHTML = `<li class="nav-item" id="nav-problems"><a class="nav-link" href="/problems">Problems</a></li>`

    let leaderboardElem = document.createElement('li');
    leaderboardElem.innerHTML = `<li class="nav-item" id="nav-leaderboard"><a class="nav-link" href="/leaderboard">Leaderboard</a></li>`

    navbarList.appendChild(homeElem);
    navbarList.appendChild(registerElem);
    navbarList.appendChild(signInElem);
    navbarList.appendChild(problemsElem);
    navbarList.appendChild(leaderboardElem);
}

function addLoggedInUserNavs() {
    const navbarList = document.getElementById('navbar-list');

    let homeElem = document.createElement('li');
    homeElem.innerHTML = `<li class="nav-item" id="nav-home"><a class="nav-link" href="/home">Home</a></li>`;

    let profileElem = document.createElement('li');
    profileElem.innerHTML = `<li class="nav-item" id="nav-profile"><a class="nav-link" href="/profile">Profile</a></li>`;

    let problemsElem = document.createElement('li');
    problemsElem.innerHTML = `<li class="nav-item" id="nav-problems"><a class="nav-link" href="/problems">Problems</a></li>`;

    let submissionsElem = document.createElement('li');
    submissionsElem.innerHTML = `<li class="nav-item" id="nav-submissions"><a class="nav-link" href="/submissions-async">Submissions</a></li>`;

    let leaderboardElem = document.createElement('li');
    leaderboardElem.innerHTML = `<li class="nav-item" id="nav-leaderboard"><a class="nav-link" href="/leaderboard">Leaderboard</a></li>`;

    navbarList.append(homeElem);
    navbarList.append(profileElem);
    navbarList.append(problemsElem);
    navbarList.append(submissionsElem);
    navbarList.append(leaderboardElem);
}

function addLoggedInAdminNavs() {
    const navbarList = document.getElementById('navbar-list');

    let homeElem = document.createElement('li');
    homeElem.innerHTML = `<li class="nav-item" id="nav-home"><a class="nav-link" href="/home">Home</a></li>`;

    let profileElem = document.createElement('li');
    profileElem.innerHTML = `<li class="nav-item" id="nav-profile"><a class="nav-link" href="/profile">Profile</a></li>`;

    let adminElem = document.createElement('li');
    adminElem.innerHTML = `<li class="nav-item" id="nav-admin"><a class="nav-link" href="/AdminPage">Admin Page</a></li>`;

    let problemsElem = document.createElement('li');
    problemsElem.innerHTML = `<li class="nav-item" id="nav-problems"><a class="nav-link" href="/problems">Problems</a></li>`;

    let submissionsElem = document.createElement('li');
    submissionsElem.innerHTML = `<li class="nav-item" id="nav-submissions"><a class="nav-link" href="/submissions-async">Submissions</a></li>`;

    let leaderboardElem = document.createElement('li');
    leaderboardElem.innerHTML = `<li class="nav-item" id="nav-leaderboard"><a class="nav-link" href="/leaderboard">Leaderboard</a></li>`;

    navbarList.appendChild(homeElem);
    navbarList.appendChild(profileElem);
    navbarList.appendChild(adminElem);
    navbarList.appendChild(problemsElem);
    navbarList.appendChild(submissionsElem);
    navbarList.appendChild(leaderboardElem);
}


function addLoggedInNotVerifiedNavs() {
    const navbarList = document.getElementById('navbar-list');

    let homeElem = document.createElement('li');
    homeElem.innerHTML = `<li class="nav-item" id="nav-home"><a class="nav-link" href="/home">Home</a></li>`;

    let verifyElem = document.createElement('li');
    verifyElem.innerHTML = `<li class="nav-item" id="nav-verify"><a class="nav-link" href="/verification/time">Account Verification</a></li>`;

    let problemsElem = document.createElement('li');
    problemsElem.innerHTML = `<li class="nav-item" id="nav-problems"><a class="nav-link" href="/problems">Problems</a></li>`;

    let leaderboardElem = document.createElement('li');
    leaderboardElem.innerHTML = `<li class="nav-item" id="nav-leaderboard"><a class="nav-link" href="/leaderboard">Leaderboard</a></li>`;

    navbarList.appendChild(homeElem);
    navbarList.appendChild(verifyElem);
    navbarList.appendChild(problemsElem);
    navbarList.appendChild(leaderboardElem);
}



// toggleElement('nav-register', !loggedIn);
// toggleElement('nav-signin', !loggedIn);
// toggleElement('nav-profile', loggedIn);
// toggleElement('nav-admin', role === "Admin");
// toggleElement('nav-submissions', loggedIn);
//
// // Attach links
// attachNavbarLinks({
//     'nav-home': '/home',
//     'nav-register': '/registration',
//     'nav-signin': '/signin',
//     'nav-profile': '/profile',
//     'nav-problems': '/problems',
//     'nav-submissions': '/submissions-async',
//     'nav-leaderboard': '/leaderboard',
//     'nav-admin': '/AdminPage'
// });

// function toggleElement(id, show) {
//     const el = document.getElementById(id);
//     if (el) {
//         el.style.display = show ? '' : 'none';
//     }
// }
//
// function attachNavbarLinks(linkMap) {
//     for (const [id, href] of Object.entries(linkMap)) {
//         const li = document.getElementById(id);
//         if (li) {
//             const anchor = li.querySelector('a');
//             if (anchor) {
//                 anchor.addEventListener('click', (e) => {
//                     e.preventDefault();
//                     window.location.href = href;
//                 });
//             }
//         }
//     }
// }
