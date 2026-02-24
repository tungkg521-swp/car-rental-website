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
