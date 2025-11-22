import React, { createContext, useContext, useEffect, useState } from "react";

const CurrencyContext = createContext();

const API_BASE = "http://localhost:8080";

export function CurrencyProvider({ children }) {
  const [currency, setCurrency] = useState("PEN"); // "PEN" o "USD"
  const [tipoCambio, setTipoCambio] = useState(1); // TC venta SUNAT
  const [loadingTc, setLoadingTc] = useState(true);
  const [errorTc, setErrorTc] = useState(null);

  useEffect(() => {
    const cargarTC = async () => {
      try {
        setLoadingTc(true);
        setErrorTc(null);
        const resp = await fetch(`${API_BASE}/tipo-cambio/venta-hoy`);
        if (!resp.ok) {
          throw new Error("No se pudo obtener el tipo de cambio");
        }
        const tc = await resp.json(); // ej: 3.78
        setTipoCambio(Number(tc));
      } catch (err) {
        console.error("Error cargando tipo de cambio:", err);
        setErrorTc("No se pudo cargar el tipo de cambio");
        setTipoCambio(1); // fallback
      } finally {
        setLoadingTc(false);
      }
    };

    cargarTC();
  }, []);

  const toggleCurrency = () => {
    setCurrency((prev) => (prev === "PEN" ? "USD" : "PEN"));
  };

  // Convierte un valor que viene en SOLES a la moneda actual
  const convertir = (valor) => {
    if (currency === "PEN") return valor;
    if (!tipoCambio || tipoCambio === 0) return valor;
    return valor / tipoCambio;
  };

  return (
    <CurrencyContext.Provider
      value={{ currency, tipoCambio, toggleCurrency, convertir, loadingTc, errorTc }}
    >
      {children}
    </CurrencyContext.Provider>
  );
}

export function useCurrency() {
  return useContext(CurrencyContext);
}
