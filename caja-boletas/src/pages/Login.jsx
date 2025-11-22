import React, { useState } from "react";
import { useAuth } from "../context/AuthContext";
import { useNavigate, useLocation } from "react-router-dom";

function Login() {
  const { login } = useAuth();
  const navigate = useNavigate();
   const location = useLocation();

  const [dni, setDni] = useState("");
  const [contrasena, setContrasena] = useState("");
  const [cargando, setCargando] = useState(false);
  const [error, setError] = useState("");

  // de dónde vino el usuario (por ejemplo, /caja)
  const from = location.state?.from?.pathname || "/";

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError("");

    if (!dni.trim() || !contrasena.trim()) {
      setError("Ingresa DNI y contraseña.");
      return;
    }

    try {
      setCargando(true);
      await login(dni, contrasena);
      navigate(from, { replace: true }); // al home después de login
    } catch (err) {
      console.error(err);
      setError(err.message || "Error al iniciar sesión");
    } finally {
      setCargando(false);
    }
  };

  return (
    <section style={{ maxWidth: "400px", margin: "20px auto" }}>
      <h2>Iniciar sesión</h2>

      <form onSubmit={handleSubmit}>
        <div className="form-row">
          <label>DNI</label>
          <input
            type="text"
            value={dni}
            onChange={(e) => setDni(e.target.value)}
          />
        </div>

        <div className="form-row">
          <label>Contraseña</label>
          <input
            type="password"
            value={contrasena}
            onChange={(e) => setContrasena(e.target.value)}
          />
        </div>

        {error && <div className="alert error">{error}</div>}

        <button type="submit" className="btn-primary" disabled={cargando}>
          {cargando ? "Ingresando..." : "Entrar"}
        </button>
      </form>
    </section>
  );
}

export default Login;
