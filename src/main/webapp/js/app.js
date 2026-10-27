/**
 * Ethiopian Elementary School Management System (Grades 1-8)
 * Frontend micro-animations, animated statistics counters, and accessibility scripts.
 */

document.addEventListener('DOMContentLoaded', () => {
    // 1. Animate Statistics Counters
    const statCounters = document.querySelectorAll('.stat-number[data-target]');
    statCounters.forEach(counter => {
        const target = parseInt(counter.getAttribute('data-target'), 10);
        if (isNaN(target)) return;

        let current = 0;
        const duration = 1200; // ms
        const stepTime = Math.max(Math.floor(duration / (target || 1)), 20);

        const timer = setInterval(() => {
            current += Math.ceil(target / (duration / stepTime));
            if (current >= target) {
                counter.innerText = target;
                clearInterval(timer);
            } else {
                counter.innerText = current;
            }
        }, stepTime);
    });

    // 2. Auto-dismiss alerts after 5 seconds
    const alerts = document.querySelectorAll('.alert-dismissible');
    alerts.forEach(alert => {
        setTimeout(() => {
            alert.classList.remove('show');
            setTimeout(() => alert.remove(), 300);
        }, 5000);
    });

    // 3. Prevent duplicate double form submissions
    const forms = document.querySelectorAll('form:not(.no-double-submit-check)');
    forms.forEach(form => {
        form.addEventListener('submit', (e) => {
            const submitBtn = form.querySelector('button[type="submit"]');
            if (submitBtn && !submitBtn.disabled) {
                // Short delay so form submission processes
                setTimeout(() => {
                    submitBtn.disabled = true;
                    submitBtn.classList.add('opacity-75');
                }, 50);
            }
        });
    });
});
