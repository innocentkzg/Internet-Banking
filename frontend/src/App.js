import "./App.css";
import QRCodeDisplay from "./components/QRCodeDisplay";

function App() {
  return (
    <div>
      <h1>Google Authenticator TOTP QR Code Generator</h1>
      <QRCodeDisplay username="test" />
    </div>
  );
}

export default App;
