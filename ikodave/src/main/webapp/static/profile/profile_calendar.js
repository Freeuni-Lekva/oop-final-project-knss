function addMonthPickerListener() {
    const monthPicker = document.getElementById('calendar-month-picker');
    monthPicker.addEventListener('change', populateCalendar);
}

function addCalendarNavListeners() {
    const prevBtn = document.getElementById('prev-month');
    const nextBtn = document.getElementById('next-month');
    const picker  = document.getElementById('calendar-month-picker');

    if (prevBtn) {
        prevBtn.addEventListener('click', () => {
            adjustMonth(picker.id, -1);
        });
    }

    if (nextBtn) {
        nextBtn.addEventListener('click', () => {
            adjustMonth(picker.id, +1);
        });
    }
}

function adjustMonth(pickerId, delta) {
    const picker = document.getElementById(pickerId);
    if (!picker) return;

    const [year, month] = picker.value.split('-').map(Number);
    const date = new Date(year, month - 1 + delta, 1);
    const newYear = date.getFullYear();
    const newMonth = String(date.getMonth() + 1).padStart(2, '0');

    picker.value = `${newYear}-${newMonth}`;
    // Force the calendar to update immediately
    populateCalendar();
}


function setDefaultMonthPicker() {
    const today = new Date();
    const year  = today.getFullYear();
    const month = String(today.getMonth() + 1).padStart(2, '0');  // zero‑pad
    const picker = document.getElementById('calendar-month-picker');
    if (picker) {
        picker.value = `${year}-${month}`;
    }
}

function populateCalendar() {
    const monthPicker = document.getElementById('calendar-month-picker');

    const parts = window.location.pathname.split('/').filter(Boolean);
    const username = parts[parts.length - 1];

    const [yearStr, monthStr] = monthPicker.value.split('-');
    const year  = Number(yearStr);
    const month = Number(monthStr);

    fetch('/api/user/profile/calendar-stats', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ username, month, year })
    })
        .then(res => res.json())
        .then(data =>  renderCalendar(data.submissionDates, month, year))
        .catch(err => console.error(err));
}

function renderCalendar(submissionDates, month, year) {
    const calendarGrid = document.getElementById('calendar-grid');
    const monthLabel   = document.querySelector('.month-label');
    calendarGrid.innerHTML = '';

    const mIndex = month - 1;

    const monthNames = [
        "January", "February", "March", "April", "May", "June",
        "July", "August", "September", "October", "November", "December"
    ];

    monthLabel.textContent = `${monthNames[mIndex]} ${year}`;

    const daysInMonth = new Date(year, mIndex + 1, 0).getDate();

    const submittedSet = new Set(submissionDates.map(ts => {
        const d = typeof ts === 'number' ? new Date(ts) : new Date(ts);
        // Use local date instead of UTC to avoid timezone issues
        const year = d.getFullYear();
        const month = String(d.getMonth() + 1).padStart(2, '0');
        const day = String(d.getDate()).padStart(2, '0');
        return `${year}-${month}-${day}`;
    }));

    const parts = window.location.pathname.split('/').filter(Boolean);
    const username = parts[parts.length - 1];

    for (let day = 1; day <= daysInMonth; day++) {
        const mm = String(month).padStart(2,'0');
        const dd = String(day).padStart(2,'0');
        const dateStr = `${year}-${mm}-${dd}`;

        const cell = document.createElement('div');
        cell.classList.add('day-cell');
        cell.dataset.date = dateStr;

        cell.dataset.day = day;
        cell.dataset.month = month;
        cell.dataset.year = year;
        
        if (submittedSet.has(dateStr)) {
            cell.classList.add('submitted');
        }

        const dayDiv = document.createElement('div');
        dayDiv.className = 'day';
        dayDiv.textContent = day;
        cell.appendChild(dayDiv);

        const monDiv = document.createElement('div');
        monDiv.className = 'mon';
        monDiv.textContent = monthNames[mIndex].slice(0,3);
        cell.appendChild(monDiv);

        calendarGrid.appendChild(cell);
    }

    // Wrap the click handler attachment in try-catch to prevent errors from blocking calendar updates
    try {
        attachCalendarDayClickHandlers(username).catch(console.error);
    } catch (error) {
        console.error('Error attaching calendar click handlers:', error);
    }
}
