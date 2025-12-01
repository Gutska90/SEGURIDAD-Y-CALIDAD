// JavaScript para login vía API y obtener JWT
// Integración con el formulario de login existente

document.addEventListener('DOMContentLoaded', function() {
    const loginForm = document.querySelector('form[action*="/login"]');
    
    if (loginForm) {
        // Agregar opción de login vía API
        loginForm.addEventListener('submit', function(e) {
            // Si hay un parámetro para usar API, usar login API
            const urlParams = new URLSearchParams(window.location.search);
            if (urlParams.get('api') === 'true') {
                e.preventDefault();
                loginAPI();
            }
            // Si no, dejar que el formulario se envíe normalmente (form-based auth)
        });
    }
});

// Función para login vía API
function loginAPI() {
    const username = document.getElementById('username').value;
    const password = document.getElementById('password').value;
    
    if (!username || !password) {
        mostrarError('Por favor, completa todos los campos');
        return;
    }
    
    fetch('/api/auth/login', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify({
            username: username,
            password: password
        })
    })
    .then(response => {
        if (!response.ok) {
            return response.json().then(data => {
                throw new Error(data.message || 'Error de autenticación');
            });
        }
        return response.json();
    })
    .then(data => {
        if (data.token) {
            // Guardar token en localStorage
            localStorage.setItem('jwt_token', data.token);
            localStorage.setItem('username', data.username);
            
            // Redirigir a inicio
            window.location.href = '/inicio';
        } else {
            mostrarError('No se recibió token de autenticación');
        }
    })
    .catch(error => {
        console.error('Error en login:', error);
        mostrarError(error.message || 'Error al iniciar sesión');
    });
}

// Función para mostrar errores
function mostrarError(mensaje) {
    // Buscar contenedor de alertas o crear uno
    let alertContainer = document.querySelector('.alert-container');
    if (!alertContainer) {
        alertContainer = document.createElement('div');
        alertContainer.className = 'alert-container';
        const loginBox = document.querySelector('.login-box');
        if (loginBox) {
            loginBox.insertBefore(alertContainer, loginBox.firstChild);
        }
    }
    
    alertContainer.innerHTML = `
        <div class="alert alert-error">
            ${mensaje}
        </div>
    `;
    
    // Remover después de 5 segundos
    setTimeout(() => {
        alertContainer.innerHTML = '';
    }, 5000);
}

// Función para verificar si hay token al cargar la página
function verificarToken() {
    const token = localStorage.getItem('jwt_token');
    if (token) {
        // Verificar si el token es válido haciendo una petición
        fetch('/api/valoraciones/receta/1', {
            headers: {
                'Authorization': `Bearer ${token}`
            }
        })
        .then(response => {
            if (response.status === 401) {
                // Token inválido o expirado
                localStorage.removeItem('jwt_token');
                localStorage.removeItem('username');
            }
        })
        .catch(error => {
            console.error('Error al verificar token:', error);
        });
    }
}

// Verificar token al cargar
verificarToken();

