import { createBrowserRouter } from "react-router-dom";
import RootLayout from "./components/RootLayout";
import DashboardPage from "./pages/DashboardPage";
import UploadPage from "./pages/UploadPage";
import CreditCardUploadPage from "./pages/CreditCardUploadPage";
import TransactionsPage from "./pages/TransactionsPage";
import RuleListPage from "./pages/RuleListPage";
import RuleFormPage from "./pages/RuleFormPage";

const router = createBrowserRouter([
  {
    path: "/",
    element: <RootLayout />,
    children: [
      { index: true, element: <DashboardPage /> },
      { path: "upload", element: <UploadPage /> },
      { path: "upload-credit-card", element: <CreditCardUploadPage /> },
      { path: "transactions", element: <TransactionsPage /> },
      { path: "rules", element: <RuleListPage /> },
      { path: "rules/new", element: <RuleFormPage mode="create" /> },
      { path: "rules/:id", element: <RuleFormPage mode="edit" /> },
    ],
  },
]);

export default router;

