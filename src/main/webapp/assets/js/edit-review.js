function openEditReviewModal(button) {
    const reviewId = button.dataset.reviewId;
    const carId = button.dataset.carId;
    const rating = button.dataset.rating;

    const commentElement = document.getElementById("review-comment-" + reviewId);
    const comment = commentElement ? commentElement.value : "";

    const reviewIdInput = document.getElementById("editReviewId");
    const carIdInput = document.getElementById("editCarId");
    const ratingInput = document.getElementById("editRating");
    const commentInput = document.getElementById("editComment");
    const modal = document.getElementById("editReviewModal");

    if (!reviewIdInput || !carIdInput || !ratingInput || !commentInput || !modal) {
        console.log("Edit review modal elements not found");
        return;
    }

    reviewIdInput.value = reviewId || "";
    carIdInput.value = carId || "";
    ratingInput.value = rating || "5";
    commentInput.value = comment || "";

    modal.classList.add("show");
}

function closeEditReviewModal() {
    const modal = document.getElementById("editReviewModal");
    if (modal) {
        modal.classList.remove("show");
    }
}

document.addEventListener("DOMContentLoaded", function () {
    const form = document.getElementById("editReviewForm");
    if (form) {
        form.addEventListener("submit", function (e) {
            const reviewId = document.getElementById("editReviewId").value.trim();
            const carId = document.getElementById("editCarId").value.trim();

            if (!reviewId || !carId) {
                e.preventDefault();
                alert("Review data is missing. Please close and open the edit popup again.");
            }
        });
    }
});

window.addEventListener("click", function (e) {
    const modal = document.getElementById("editReviewModal");
    if (e.target === modal) {
        closeEditReviewModal();
    }
});

window.addEventListener("keydown", function (e) {
    if (e.key === "Escape") {
        closeEditReviewModal();
    }
});