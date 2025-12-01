// JavaScript para manejo de autenticación JWT
// Guardar token después de login

// Función para guardar token después de login exitoso
function guardarToken(token) {
    localStorage.setItem('jwt_token', token);
}

// Función para obtener token
function obtenerToken() {
    return localStorage.getItem('jwt_token');
}

// Función para eliminar token (logout)
function eliminarToken() {
    localStorage.removeItem('jwt_token');
}

// Interceptar login form y guardar token
document.addEventListener('DOMContentLoaded', function() {
    const loginForm = document.querySelector('form[action*="/login"]');
    if (loginForm) {
        // Si hay un formulario de login, intentar obtener token de la respuesta
        // Nota: Esto requiere que el backend retorne el token en algún lugar
        // Por ahora, el login usa form-based auth, no JWT directamente
    }
});

// Función para hacer login vía API y obtener JWT
function loginAPI(username, password) {
    return fetch('/api/auth/login', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify({
            username: username,
            password: password
        })
    })
    .then(response => response.json())
    .then(data => {
        if (data.token) {
            guardarToken(data.token);
            return data;
        } else {
            throw new Error('No se recibió token');
        }
    });
}

