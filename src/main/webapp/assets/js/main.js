const carousel = document.getElementById('promoCarousel');
const next = document.getElementById('promoNext');
const prev = document.getElementById('promoPrev');

if (carousel && next && prev) {
  next.onclick = () =>
    carousel.scrollBy({ left: 400, behavior: 'smooth' });

  prev.onclick = () =>
    carousel.scrollBy({ left: -400, behavior: 'smooth' });
}
