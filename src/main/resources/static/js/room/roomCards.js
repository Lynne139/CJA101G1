function showRoomTypeModal(button) {
    const name = button.getAttribute('data-name');
    const price = button.getAttribute('data-price');
    const content = button.getAttribute('data-content');
    const img = button.getAttribute('data-img');
    
    document.getElementById('modalRoomTypeName').textContent = name;
    document.getElementById('modalRoomTypePrice').textContent = price;
    document.getElementById('modalRoomTypeImg').src = img;
    document.getElementById('modalRoomTypeContent').innerHTML = content;
    
    const modal = new bootstrap.Modal(document.getElementById('roomTypeModal'));
    modal.show();
}