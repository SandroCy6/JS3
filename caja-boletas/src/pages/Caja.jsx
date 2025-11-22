import React from "react";
import { useCart } from "../context/CartContext";
import { useCurrency } from "../context/CurrencyContext";
import BoletaForm from "../componentes/BoletaForm";

function Caja() {
  const { cart } = useCart();
  const { currency, convertir } = useCurrency();

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

  return (
  <section className="section-box">
    <h1>Caja - Generar compra</h1>

    {cart.length === 0 ? (
      <p>No hay productos en el carrito.</p>
    ) : (
      <>
        <h3>Resumen de productos</h3>
        {cart.map((item) => (
          <div
            key={item.id}
            style={{
              marginBottom: "10px",
              paddingBottom: "6px",
              borderBottom: "1px solid #e5e7eb",
            }}
          >
            <strong>{item.nombre}</strong> <br />
            Cantidad: {item.cantidad} <br />
            Precio unitario: {formatPrice(item.precio)} <br />
            Subtotal: {formatPrice(item.precio * item.cantidad)} <br />
          </div>
        ))}
        <hr style={{ margin: "12px 0" }} />
        <h3>Total a pagar: {formatPrice(total)}</h3>
      </>
    )}

    <hr style={{ margin: "18px 0" }} />

    <BoletaForm cart={cart} />
  </section>
);

}

export default Caja;
