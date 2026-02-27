let currentIndex = 1;
const totalImages = 5;

// Lấy các biến từ DOM
const mainImage = document.getElementById("mainCarImage");

// Lấy imageFolder từ src ban đầu
const basePath = mainImage.src.replace(/_\d+\.jpg$/, "");

/* =========================
   ĐỔI ẢNH
========================= */
function updateImage(index) {
    currentIndex = index;
    mainImage.src = basePath + "_" + index + ".jpg";

    // active thumbnail
    document.querySelectorAll(".thumbs img").forEach((img, i) => {
        img.classList.toggle("active", i + 1 === index);
    });
}

/* =========================
   CLICK THUMB
========================= */
function changeImage(index) {
    updateImage(index);
}

/* =========================
   NEXT / PREV
========================= */
function nextImage() {
    let next = currentIndex + 1;
    if (next > totalImages) next = 1;
    updateImage(next);
}

function prevImage() {
    let prev = currentIndex - 1;
    if (prev < 1) prev = totalImages;
    updateImage(prev);
}

/* =========================
   INIT
========================= */
document.addEventListener("DOMContentLoaded", () => {
    updateImage(1);
});
