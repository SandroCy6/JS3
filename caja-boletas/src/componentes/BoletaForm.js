import React, { useState, useEffect } from "react";
import { fetchProductos } from "../api";

const API_BASE = "http://34.46.167.111:8080";

function BoletaForm({ cart = [] }) {
  const [tipoDocumento, setTipoDocumento] = useState("DNI");
  const [numeroDocumento, setNumeroDocumento] = useState("");
  const [moneda, setMoneda] = useState("PEN");

  // ahora cada item también puede tener "nombre" para el buscador
 const [items, setItems] = useState([
  { productoId: "", cantidad: 1, nombre: "", error: null },
]);


  const [loading, setLoading] = useState(false);
  const [error, setError] = useState("");
  const [boleta, setBoleta] = useState(null);

  // productos desde el backend para buscar por nombre
  const [productos, setProductos] = useState([]);

  //  cargar productos para el datalist (buscador por nombre) 
  useEffect(() => {
    const cargar = async () => {
      try {
        const data = await fetchProductos();
        setProductos(data);
      } catch (err) {
        console.error("Error al cargar productos para BoletaForm:", err);
      }
    };
    cargar();
  }, []);

  // --- precargar items desde el carrito cuando venimos de "Generar compra" ---
  useEffect(() => {
    if (cart && cart.length > 0) {
      const desdeCarrito = cart.map((item) => ({
  productoId: item.id,
  cantidad: item.cantidad,
  nombre: item.nombre,
  error: null,
}));
      setItems(desdeCarrito);
    }
  }, [cart]);

  // --- manejo de líneas de productos ---

const handleItemChange = (index, field, value) => {
  const nuevos = [...items];
  let itemActualizado = { ...nuevos[index], [field]: value };

  // 🔵 Validación por nombre
  if (field === "nombre") {
    const encontrado = productos.find(
      (p) => p.nombre.toLowerCase() === value.toLowerCase()
    );

    if (encontrado) {
      // Si el nombre existe → rellenar ID
      itemActualizado.productoId = encontrado.id;
      itemActualizado.error = null;
    } else {
      // Si el nombre NO existe → marcar error
      itemActualizado.productoId = "";
      itemActualizado.error = "Producto no encontrado";
    }
  }

  // 🔵 Validación por ID
  if (field === "productoId") {
    const idNum = Number(value);
    const encontrado = productos.find((p) => p.id === idNum);

    if (encontrado) {
      // Si el ID existe → rellenar nombre
      itemActualizado.nombre = encontrado.nombre;
      itemActualizado.error = null;
    } else {
      // ID inválido → no rellenar nombre
      itemActualizado.nombre = "";
      itemActualizado.error = "Producto no encontrado";
    }
  }

  nuevos[index] = itemActualizado;
  setItems(nuevos);
};


  const agregarLinea = () => {
    setItems([...items, { productoId: "", cantidad: 1, nombre: "" }]);
  };

  const eliminarLinea = (index) => {
    if (items.length === 1) return; // al menos una fila
    const nuevos = items.filter((_, i) => i !== index);
    setItems(nuevos);
  };

  // --- submit ---

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError("");
    setBoleta(null);

    // limpiar y validar un poco
    const itemsLimpios = items
      .map((it) => ({
        productoId: Number(it.productoId),
        cantidad: Number(it.cantidad),
      }))
      .filter((it) => it.productoId && it.cantidad > 0);

    if (!numeroDocumento.trim()) {
      setError("Ingresa el número de documento.");
      return;
    }

    if (itemsLimpios.length === 0) {
      setError("Agrega al menos un producto válido.");
      return;
    }

    const body = {
      tipoDocumento,
      numeroDocumento,
      moneda,
      items: itemsLimpios,
    };

    try {
      setLoading(true);

      const resp = await fetch(`${API_BASE}/boletas`, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(body),
      });

      if (!resp.ok) {
        const texto = await resp.text();
        throw new Error(texto || "Error al generar la boleta");
      }

      const data = await resp.json();
      setBoleta(data);
    } catch (err) {
      console.error(err);
      setError(err.message);
    } finally {
      setLoading(false);
    }
  };

  const abrirPdf = () => {
    if (!boleta) return;
    const url = `${API_BASE}/boletas/${boleta.id}/pdf`;
    window.open(url, "_blank");
  };

  return (
    <div className="boleta-card">
      <form onSubmit={handleSubmit}>
        <div className="form-row">
          <label>Tipo documento</label>
          <select
            value={tipoDocumento}
            onChange={(e) => setTipoDocumento(e.target.value)}
          >
            <option value="DNI">DNI</option>
            <option value="RUC">RUC</option>
          </select>
        </div>

        <div className="form-row">
          <label>Número documento</label>
          <input
            type="text"
            value={numeroDocumento}
            onChange={(e) => setNumeroDocumento(e.target.value)}
          />
        </div>

        <div className="form-row">
          <label>Moneda</label>
          <select value={moneda} onChange={(e) => setMoneda(e.target.value)}>
            <option value="PEN">PEN</option>
            <option value="USD">USD</option>
          </select>
        </div>

        <h3>Productos</h3>
        <table className="items-table">
          <thead>
            <tr>
              <th>Nombre producto</th>
              <th>ID Producto</th>
              <th>Cantidad</th>
              <th></th>
            </tr>
          </thead>
                    <tbody>
            {items.map((it, index) => (
              <React.Fragment key={index}>
                <tr>
                  <td>
                    {/* buscador por nombre usando datalist */}
                    <input
                      list="lista-productos"
                      type="text"
                      value={it.nombre || ""}
                      onChange={(e) =>
                        handleItemChange(index, "nombre", e.target.value)
                      }
                    />
                  </td>
                  <td>
                    <input
                      type="number"
                      value={it.productoId}
                      onChange={(e) =>
                        handleItemChange(index, "productoId", e.target.value)
                      }
                    />
                  </td>
                  <td>
                    <input
                      type="number"
                      min="1"
                      value={it.cantidad}
                      onChange={(e) =>
                        handleItemChange(index, "cantidad", e.target.value)
                      }
                    />
                  </td>
                  <td>
                    <button
                      type="button"
                      className="btn-secondary"
                      onClick={() => eliminarLinea(index)}
                    >
                      X
                    </button>
                  </td>
                </tr>

                {it.error && (
                  <tr>
                    <td colSpan="4" style={{ color: "red", padding: "4px 0" }}>
                      {it.error}
                    </td>
                  </tr>
                )}
              </React.Fragment>
            ))}
          </tbody>
        </table>

        {/* opciones para el buscador por nombre */}
        <datalist id="lista-productos">
          {productos.map((p) => (
            <option key={p.id} value={p.nombre} />
          ))}
        </datalist>

        <button
          type="button"
          className="btn-secondary"
          onClick={agregarLinea}
        >
          + Agregar línea
        </button>

        {error && <div className="alert error">{error}</div>}

        <button type="submit" className="btn-primary" disabled={loading}>
          {loading ? "Generando..." : "Generar comprobante"}
        </button>
      </form>

      {boleta && (
        <div className="resultado">
          <h3>Comprobante generado</h3>
          <p>
            <strong>{boleta.tipoComprobante}</strong> N°{" "}
            <strong>{boleta.numeroBoleta}</strong>
          </p>
          <p>Fecha: {boleta.fecha}</p>
          <p>
            Moneda: {boleta.moneda} | Tipo de cambio: {boleta.tipoCambio}
          </p>
          <p>
            Total en soles: {boleta.totalSoles} | Total en dólares:{" "}
            {boleta.totalDolares}
          </p>

          {boleta.cliente && (
            <>
              <h4>Cliente</h4>
              <p>Tipo doc: {boleta.cliente.tipoDocumento}</p>
              <p>Número: {boleta.cliente.numeroDocumento}</p>
              {boleta.cliente.razonSocial ? (
                <p>Razón social: {boleta.cliente.razonSocial}</p>
              ) : (
                <p>
                  {boleta.cliente.nombres} {boleta.cliente.apellidos}
                </p>
              )}
            </>
          )}

          {boleta.productos && boleta.productos.length > 0 && (
            <>
              <h4>Detalle</h4>
              <table className="items-table">
                <thead>
                  <tr>
                    <th>Producto</th>
                    <th>Cant.</th>
                    <th>Precio</th>
                    <th>Subtotal</th>
                  </tr>
                </thead>
                <tbody>
                  {boleta.productos.map((p) => (
                    <tr key={p.productoId}>
                      <td>{p.nombre}</td>
                      <td>{p.cantidad}</td>
                      <td>{p.precioUnitario}</td>
                      <td>{p.subtotal}</td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </>
          )}

          <button className="btn-primary" onClick={abrirPdf}>
            Ver PDF
          </button>
        </div>
      )}
    </div>
  );
}

export default BoletaForm;
