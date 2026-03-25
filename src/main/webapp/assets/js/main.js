document.addEventListener("DOMContentLoaded", () => {
  const carousel = document.getElementById("promoCarousel");
  const prev = document.getElementById("promoPrev");
  const next = document.getElementById("promoNext");

  if (!carousel || !prev || !next) return;

  const scrollAmount = carousel.offsetWidth;

  next.addEventListener("click", () => {
    carousel.scrollBy({
      left: scrollAmount,
      behavior: "smooth"
    });
  });

  prev.addEventListener("click", () => {
    carousel.scrollBy({
      left: -scrollAmount,
      behavior: "smooth"
    });
  });
});

(function () {
    const startInput = document.getElementById("startDate");
    const endInput = document.getElementById("endDate");

    if (!startInput || !endInput) {
        return;
    }

    const today = new Date().toISOString().split("T")[0];
    startInput.min = today;
    endInput.min = today;

    startInput.addEventListener("change", function () {
        endInput.min = startInput.value || today;

        if (endInput.value && endInput.value <= startInput.value) {
            endInput.value = "";
        }
    });
})();

function toggleNotification() {
                const popup = document.querySelector(".notification-popup");
                popup.classList.toggle("show");
            }