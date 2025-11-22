import React from "react";
import { Link, useNavigate } from "react-router-dom";
import { useCart } from "../context/CartContext";
import { useAuth } from "../context/AuthContext";
import { useCurrency } from "../context/CurrencyContext";
import "./Header.css";

function Header() {
  const { cart } = useCart();
  const { user, logout } = useAuth();
  const { currency, toggleCurrency, loadingTc, errorTc } = useCurrency();
  const navigate = useNavigate();

  const totalItems = cart.reduce(
    (sum, item) => sum + (item.cantidad || 1),
    0
  );

  const handleLogout = () => {
    logout();
    navigate("/");
  };

  const labelMoneda =
    currency === "PEN" ? "Moneda: S/" : "Moneda: $";

  return (
    <header className="header">
      <div className="header-left">
        <Link to="/" className="logo">
          TiendaTechB
        </Link>
      </div>

      <nav className="header-right">
        {/* Botón de moneda */}
        <button
          type="button"
          className="btn-nav"
          onClick={toggleCurrency}
          disabled={loadingTc}
        >
          {labelMoneda}
        </button>

        {errorTc && (
          <span style={{ color: "red", fontSize: "0.8rem", marginRight: "8px" }}>
            TC no disponible
          </span>
        )}

        <Link to="/caja" className="btn-nav">Caja</Link>

        {user ? (
          <>
            <span className="user-label">
              Hola, {user.nombres} {user.apellidos}
            </span>
            <button className="btn-nav" onClick={handleLogout}>
              Cerrar sesión
            </button>
          </>
        ) : (
          <>
            <Link to="/login" className="btn-nav">
              Iniciar sesión
            </Link>
            <Link to="/registro" className="btn-nav">
              Registrarse
            </Link>
          </>
        )}

        <Link to="/carrito" className="btn-cart">
          🛒 Carrito ({totalItems})
        </Link>
      </nav>
    </header>
  );
}

export default Header;
