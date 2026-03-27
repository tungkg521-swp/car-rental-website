  const promoCards = document.querySelectorAll('.promo-card');
    const promoModal = document.getElementById('promoModal');
    const closePromoModal = document.getElementById('closePromoModal');

    const promoModalTitle = document.getElementById('promoModalTitle');
    const promoModalSubtitle = document.getElementById('promoModalSubtitle');
    const promoModalContent = document.getElementById('promoModalContent');

    promoCards.forEach(card => {
        card.addEventListener('click', function () {
            promoModalTitle.textContent = this.dataset.title;
            promoModalSubtitle.textContent = this.dataset.subtitle;
            promoModalContent.textContent = this.dataset.content;

            promoModal.classList.add('show');
            document.body.classList.add('modal-open');
        });
    });

    function closeModal() {
        promoModal.classList.remove('show');
        document.body.classList.remove('modal-open');
    }

    closePromoModal.addEventListener('click', closeModal);
    promoModal.querySelector('.promo-modal-overlay').addEventListener('click', closeModal);

    document.addEventListener('keydown', function (e) {
        if (e.key === 'Escape') {
            closeModal();
        }
    });