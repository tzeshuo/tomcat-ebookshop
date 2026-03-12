function escapeHtml(value) {
  return String(value)
    .replace(/&/g, "&amp;")
    .replace(/</g, "&lt;")
    .replace(/>/g, "&gt;")
    .replace(/"/g, "&quot;")
    .replace(/'/g, "&#39;");
}

function setAuthControls(user) {
  const authControls = document.getElementById("authControls");
  if (!authControls) {
    return;
  }

  if (user) {
    authControls.innerHTML =
      '<div class="user-menu">' +
      '<span class="user-trigger">&#x1F464; ' + escapeHtml(user) + "</span>" +
      '<div class="user-menu-content">' +
      '<a href="eshoplogout">Logout</a>' +
      "</div>" +
      "</div>";
    return;
  }

  authControls.innerHTML =
    '<a class="auth-link" href="login.html">Login</a>' +
    '<a class="auth-link" href="register.html">Register</a>';
}

function fetchSessionUser() {
  return fetch("authstatus", { headers: { Accept: "application/json" } })
    .then(function (response) {
      if (!response.ok) {
        throw new Error("Unable to load auth status.");
      }
      return response.json();
    })
    .then(function (data) {
      return data && data.user ? data.user : null;
    });
}
