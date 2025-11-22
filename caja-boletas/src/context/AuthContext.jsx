import React, { createContext, useContext, useEffect, useState } from "react";

const AuthContext = createContext();

const API_BASE = "http://localhost:8080";

export function AuthProvider({ children }) {
  const [user, setUser] = useState(null);

  useEffect(() => {
    const guardado = localStorage.getItem("user");
    if (guardado) {
      setUser(JSON.parse(guardado));
    }
  }, []);

  // 🔐 LOGIN: usa /auth/login con { dni, contrasena }
  const login = async (dni, contrasena) => {
    const resp = await fetch(`${API_BASE}/auth/login`, {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({ dni, contrasena }),
    });

    if (!resp.ok) {
      const texto = await resp.text();
      throw new Error(texto || "Error al iniciar sesión");
    }

    const data = await resp.json(); // esto es UsuarioResponse
    setUser(data);
    localStorage.setItem("user", JSON.stringify(data));
  };

  // 🚪 LOGOUT
  const logout = () => {
    setUser(null);
    localStorage.removeItem("user");
  };

  // 📝 REGISTRO: usa /auth/registro con { dni, contrasena }
  const register = async ({ dni, contrasena }) => {
    const resp = await fetch(`${API_BASE}/auth/registro`, {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({ dni, contrasena }),
    });

    if (!resp.ok) {
      const texto = await resp.text();
      throw new Error(texto || "Error al registrarse");
    }

    const data = await resp.json(); // UsuarioResponse
    return data;
  };

  return (
    <AuthContext.Provider value={{ user, login, logout, register }}>
      {children}
    </AuthContext.Provider>
  );
}

export function useAuth() {
  return useContext(AuthContext);
}
