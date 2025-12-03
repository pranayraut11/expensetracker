import React, { useState } from 'react'
import { uploadFile } from '../services/transactionApi'

const UploadPage = () => {
  const [file, setFile] = useState(null)
  const [uploading, setUploading] = useState(false)
  const [result, setResult] = useState(null)
  const [error, setError] = useState(null)

  const handleFileChange = (e) => {
    const selectedFile = e.target.files[0]
    if (selectedFile) {
      setFile(selectedFile)
      setResult(null)
      setError(null)
    }
  }

  const handleSubmit = async (e) => {
    e.preventDefault()

    if (!file) {
      setError('Please select a file')
      return
    }

    setUploading(true)
    setError(null)
    setResult(null)

    try {
      const formData = new FormData()
      formData.append('file', file)

      const response = await uploadFile(formData)
      setResult(response)
      setFile(null)
      // Reset file input
      e.target.reset()
    } catch (err) {
      console.error('Upload error:', err)
      setError(err.response?.data?.error || err.response?.data || 'Failed to upload file')
    } finally {
      setUploading(false)
    }
  }

  return (
    <div className="max-w-2xl mx-auto">
      <div className="bg-white rounded-lg shadow-md p-6">
        <h2 className="text-2xl font-bold text-gray-800 mb-6">Upload Bank Statement</h2>

        <form onSubmit={handleSubmit}>
          <div className="mb-6">
            <label className="block text-sm font-medium text-gray-700 mb-2">
              Select Excel File (.xls or .xlsx)
            </label>
            <input
              type="file"
              accept=".xls,.xlsx"
              onChange={handleFileChange}
              className="block w-full text-sm text-gray-500 file:mr-4 file:py-2 file:px-4 file:rounded-md file:border-0 file:text-sm file:font-semibold file:bg-indigo-50 file:text-indigo-700 hover:file:bg-indigo-100"
              disabled={uploading}
            />
          </div>

          {file && (
            <div className="mb-4 p-3 bg-blue-50 border border-blue-200 rounded-md">
              <p className="text-sm text-blue-800">
                Selected file: <span className="font-medium">{file.name}</span>
              </p>
            </div>
          )}

          <button
            type="submit"
            disabled={!file || uploading}
            className="w-full bg-indigo-600 text-white py-2 px-4 rounded-md hover:bg-indigo-700 disabled:bg-gray-400 disabled:cursor-not-allowed font-medium transition"
          >
            {uploading ? 'Uploading...' : 'Upload and Process'}
          </button>
        </form>

        {error && (
          <div className="mt-6 p-4 bg-red-50 border border-red-200 rounded-md">
            <p className="text-red-800 text-sm">
              <strong>Error:</strong> {error}
            </p>
          </div>
        )}

        {result && (
          <div className="mt-6 space-y-4">
            {/* Success Summary */}
            <div className="p-4 bg-green-50 border border-green-200 rounded-md">
              <h3 className="text-lg font-semibold text-green-800 mb-2">Upload Successful!</h3>
              <div className="text-sm text-green-700 space-y-1">
                <p>✅ Rows processed: <span className="font-medium">{result.rowsProcessed}</span></p>
                <p>✅ Rows saved: <span className="font-medium">{result.rowsSaved}</span></p>
                {result.duplicates > 0 && (
                  <p>🔄 Duplicates skipped: <span className="font-medium">{result.duplicates}</span></p>
                )}
                {result.errors > 0 && (
                  <p>⚠️ Errors: <span className="font-medium">{result.errors}</span></p>
                )}
              </div>
            </div>

            {/* Duplicate Details */}
            {result.duplicates > 0 && result.duplicateTransactions && result.duplicateTransactions.length > 0 && (
              <div className="p-4 bg-yellow-50 border border-yellow-200 rounded-md">
                <h4 className="text-md font-semibold text-yellow-800 mb-3">
                  Duplicate Transactions Detected ({result.duplicates})
                </h4>
                <p className="text-xs text-yellow-700 mb-3">
                  The following transactions already exist in the database and were not imported:
                </p>
                <div className="max-h-64 overflow-y-auto">
                  <ul className="space-y-2">
                    {result.duplicateTransactions.map((dup, index) => (
                      <li key={index} className="text-xs text-yellow-800 bg-yellow-100 p-2 rounded border border-yellow-300">
                        {dup}
                      </li>
                    ))}
                  </ul>
                </div>
              </div>
            )}
          </div>
        )}

        <div className="mt-8 p-4 bg-gray-50 rounded-md">
          <h3 className="font-semibold text-gray-800 mb-2">Expected Excel Format:</h3>
          <div className="overflow-x-auto">
            <table className="min-w-full text-xs border border-gray-300">
              <thead className="bg-gray-200">
                <tr>
                  <th className="border border-gray-300 px-2 py-1">Date</th>
                  <th className="border border-gray-300 px-2 py-1">Description</th>
                  <th className="border border-gray-300 px-2 py-1">Amount</th>
                  <th className="border border-gray-300 px-2 py-1">Type</th>
                  <th className="border border-gray-300 px-2 py-1">Balance</th>
                </tr>
              </thead>
              <tbody>
                <tr>
                  <td className="border border-gray-300 px-2 py-1">01-01-2025</td>
                  <td className="border border-gray-300 px-2 py-1">UPI/1234/Swiggy</td>
                  <td className="border border-gray-300 px-2 py-1">-250</td>
                  <td className="border border-gray-300 px-2 py-1">DEBIT</td>
                  <td className="border border-gray-300 px-2 py-1">12000</td>
                </tr>
                <tr>
                  <td className="border border-gray-300 px-2 py-1">01-01-2025</td>
                  <td className="border border-gray-300 px-2 py-1">Salary</td>
                  <td className="border border-gray-300 px-2 py-1">50000</td>
                  <td className="border border-gray-300 px-2 py-1">CREDIT</td>
                  <td className="border border-gray-300 px-2 py-1">62000</td>
                </tr>
              </tbody>
            </table>
          </div>
        </div>
      </div>
    </div>
  )
}

export default UploadPage

