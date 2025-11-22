import { BrowserRouter as Router, Routes, Route } from "react-router-dom";
import Header from "./componentes/Header";
import Home from "./pages/Home";
import Carrito from "./pages/Carrito";
import Caja from "./pages/Caja";
import Login from "./pages/Login";
import Register from "./pages/Register";
import { CartProvider } from "./context/CartContext";
import { AuthProvider } from "./context/AuthContext";
import { CurrencyProvider } from "./context/CurrencyContext"; // lo usaremos en la parte 2
import RequireAuth from "./componentes/RequireAuth";
import "./App.css";

function App() {
  return (
    <AuthProvider>
      <CartProvider>
        <CurrencyProvider>
          <Router>
            <Header />

            <main className="app-main">
              <Routes>
                <Route path="/" element={<Home />} />
                <Route path="/carrito" element={<Carrito />} />
                <Route
                  path="/caja"
                  element={
                    <RequireAuth>
                      <Caja />
                    </RequireAuth>
                  }
                />
                <Route path="/login" element={<Login />} />
                <Route path="/registro" element={<Register />} />
              </Routes>
            </main>
          </Router>
        </CurrencyProvider>
      </CartProvider>
    </AuthProvider>
  );
}

export default App;
