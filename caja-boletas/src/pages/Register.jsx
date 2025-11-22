import React, { useState } from "react";
import { useAuth } from "../context/AuthContext";
import { useNavigate } from "react-router-dom";

function Register() {
  const { register } = useAuth();
  const navigate = useNavigate();

  const [dni, setDni] = useState("");
  const [contrasena, setContrasena] = useState("");
  const [confirmar, setConfirmar] = useState("");
  const [cargando, setCargando] = useState(false);
  const [error, setError] = useState("");
  const [mensajeOk, setMensajeOk] = useState("");

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError("");
    setMensajeOk("");

    if (!dni.trim() || !contrasena.trim() || !confirmar.trim()) {
      setError("Completa todos los campos.");
      return;
    }

    if (contrasena !== confirmar) {
      setError("Las contraseñas no coinciden.");
      return;
    }

    try {
      setCargando(true);

      await register({ dni, contrasena });

      setMensajeOk("Usuario registrado correctamente.");
      setTimeout(() => navigate("/login"), 1000);
    } catch (err) {
      console.error(err);
      setError(err.message || "Error al registrarse");
    } finally {
      setCargando(false);
    }
  };

  return (
    <section style={{ maxWidth: "400px", margin: "20px auto" }}>
      <h2>Registrarse</h2>

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

        <div className="form-row">
          <label>Confirmar contraseña</label>
          <input
            type="password"
            value={confirmar}
            onChange={(e) => setConfirmar(e.target.value)}
          />
        </div>

        {error && <div className="alert error">{error}</div>}
        {mensajeOk && <div className="alert success">{mensajeOk}</div>}

        <button type="submit" className="btn-primary" disabled={cargando}>
          {cargando ? "Registrando..." : "Crear cuenta"}
        </button>
      </form>
    </section>
  );
}

export default Register;
