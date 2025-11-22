import React from "react";
import { useCart } from "../context/CartContext";
import { useCurrency } from "../context/CurrencyContext";
import { useNavigate } from "react-router-dom";

function Carrito() {
  const { cart, removeFromCart, clearCart } = useCart();
  const { currency, convertir } = useCurrency();
  const navigate = useNavigate();

  const formatPrice = (valor) => {
    const convertido = convertir(valor);
    if (currency === "PEN") {
      return `S/ ${convertido.toFixed(2)}`;
    }
    return `$ ${convertido.toFixed(2)}`;
  };

  const total = cart.reduce(
    (sum, item) => sum + item.precio * item.cantidad,
    0
  );

  const handleGenerarCompra = () => {
    navigate("/caja");
  };

  if (cart.length === 0) {
    return (
      <section style={{ padding: "20px" }}>
        <h1>Carrito de compras</h1>
        <p>Tu carrito está vacío.</p>
      </section>
    );
  }

 return (
  <section className="section-box">
    <h1>Carrito de compras</h1>

    {cart.map((item) => (
      <div
        key={item.id}
        style={{
          marginBottom: "12px",
          paddingBottom: "8px",
          borderBottom: "1px solid #e5e7eb",
        }}
      >
        <strong>{item.nombre}</strong> <br />
        Cantidad: {item.cantidad} <br />
        Precio unitario: {formatPrice(item.precio)} <br />
        Subtotal: {formatPrice(item.precio * item.cantidad)} <br />
        <button
          className="btn-secondary"
          style={{ marginTop: "4px" }}
          onClick={() => removeFromCart(item.id)}
        >
          Quitar
        </button>
      </div>
    ))}

    <hr style={{ margin: "12px 0" }} />
    <h3>Total: {formatPrice(total)}</h3>

    <div style={{ marginTop: "10px" }}>
      <button
        className="btn-secondary"
        style={{ marginRight: "8px" }}
        onClick={clearCart}
      >
        Vaciar carrito
      </button>

      <button className="btn-primary" onClick={handleGenerarCompra}>
        Generar compra
      </button>
    </div>
  </section>
);
}

export default Carrito;
