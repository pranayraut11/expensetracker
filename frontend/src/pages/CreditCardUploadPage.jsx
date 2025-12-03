import React, { useState } from 'react'
import { uploadCreditCardStatement } from '../services/creditCardApi'

const CreditCardUploadPage = () => {
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

  const handleDrop = (e) => {
    e.preventDefault()
    const droppedFile = e.dataTransfer.files[0]
    if (droppedFile) {
      setFile(droppedFile)
      setResult(null)
      setError(null)
    }
  }

  const handleDragOver = (e) => {
    e.preventDefault()
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

      const response = await uploadCreditCardStatement(formData)
      setResult(response)
      setFile(null)
      e.target.reset()
    } catch (err) {
      console.error('Upload error:', err)
      setError(err.response?.data || 'Failed to upload credit card statement')
    } finally {
      setUploading(false)
    }
  }

  return (
    <div className="max-w-3xl mx-auto">
      <div className="bg-white rounded-lg shadow-md p-6">
        <h2 className="text-2xl font-bold text-gray-800 mb-2">Upload Credit Card Statement</h2>
        <p className="text-sm text-gray-600 mb-6">
          Upload your credit card statement in Excel format (.xls or .xlsx)
        </p>

        <form onSubmit={handleSubmit}>
          {/* Drag & Drop Zone */}
          <div
            onDrop={handleDrop}
            onDragOver={handleDragOver}
            className="mb-6 border-2 border-dashed border-gray-300 rounded-lg p-8 text-center hover:border-indigo-500 transition-colors cursor-pointer"
          >
            <input
              type="file"
              accept=".xls,.xlsx"
              onChange={handleFileChange}
              className="hidden"
              id="cc-file-upload"
              disabled={uploading}
            />
            <label htmlFor="cc-file-upload" className="cursor-pointer">
              <div className="flex flex-col items-center">
                <svg className="w-16 h-16 text-gray-400 mb-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M7 16a4 4 0 01-.88-7.903A5 5 0 1115.9 6L16 6a5 5 0 011 9.9M15 13l-3-3m0 0l-3 3m3-3v12" />
                </svg>
                <p className="text-lg font-medium text-gray-700 mb-1">
                  Drag & drop your file here
                </p>
                <p className="text-sm text-gray-500 mb-2">or click to browse</p>
                <p className="text-xs text-gray-400">Supports .xls and .xlsx files (Max 10MB)</p>
              </div>
            </label>
          </div>

          {file && (
            <div className="mb-4 p-4 bg-blue-50 border border-blue-200 rounded-md flex items-center justify-between">
              <div className="flex items-center">
                <svg className="w-6 h-6 text-blue-600 mr-3" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M9 12h6m-6 4h6m2 5H7a2 2 0 01-2-2V5a2 2 0 012-2h5.586a1 1 0 01.707.293l5.414 5.414a1 1 0 01.293.707V19a2 2 0 01-2 2z" />
                </svg>
                <div>
                  <p className="text-sm font-medium text-blue-800">{file.name}</p>
                  <p className="text-xs text-blue-600">{(file.size / 1024).toFixed(2)} KB</p>
                </div>
              </div>
              <button
                type="button"
                onClick={() => setFile(null)}
                className="text-blue-600 hover:text-blue-800"
                disabled={uploading}
              >
                <svg className="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M6 18L18 6M6 6l12 12" />
                </svg>
              </button>
            </div>
          )}

          <button
            type="submit"
            disabled={!file || uploading}
            className="w-full bg-indigo-600 text-white py-3 px-4 rounded-md hover:bg-indigo-700 disabled:bg-gray-400 disabled:cursor-not-allowed font-medium transition flex items-center justify-center"
          >
            {uploading ? (
              <>
                <svg className="animate-spin -ml-1 mr-3 h-5 w-5 text-white" fill="none" viewBox="0 0 24 24">
                  <circle className="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" strokeWidth="4"></circle>
                  <path className="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path>
                </svg>
                Processing...
              </>
            ) : (
              'Upload Credit Card Statement'
            )}
          </button>
        </form>

        {error && (
          <div className="mt-6 p-4 bg-red-50 border border-red-200 rounded-md">
            <div className="flex items-start">
              <svg className="w-5 h-5 text-red-600 mt-0.5 mr-2" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M12 8v4m0 4h.01M21 12a9 9 0 11-18 0 9 9 0 0118 0z" />
              </svg>
              <div>
                <p className="text-sm font-medium text-red-800">Error</p>
                <p className="text-sm text-red-700 mt-1">{error}</p>
              </div>
            </div>
          </div>
        )}

        {result && (
          <div className="mt-6 space-y-4">
            {/* Success Summary */}
            <div className="p-4 bg-green-50 border border-green-200 rounded-md">
              <div className="flex items-start">
                <svg className="w-6 h-6 text-green-600 mt-0.5 mr-3" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M9 12l2 2 4-4m6 2a9 9 0 11-18 0 9 9 0 0118 0z" />
                </svg>
                <div className="flex-1">
                  <h3 className="text-lg font-semibold text-green-800 mb-2">Upload Successful!</h3>
                  <div className="text-sm text-green-700 space-y-1">
                    <p>✅ Rows processed: <span className="font-medium">{result.rowsProcessed}</span></p>
                    <p>✅ Transactions saved: <span className="font-medium">{result.rowsSaved}</span></p>
                    {result.duplicates > 0 && (
                      <p>🔄 Duplicates skipped: <span className="font-medium">{result.duplicates}</span></p>
                    )}
                    {result.errors > 0 && (
                      <p>⚠️ Errors: <span className="font-medium">{result.errors}</span></p>
                    )}
                  </div>
                </div>
              </div>
            </div>

            {/* Duplicate Details */}
            {result.duplicates > 0 && result.duplicateTransactions && result.duplicateTransactions.length > 0 && (
              <div className="p-4 bg-yellow-50 border border-yellow-200 rounded-md">
                <h4 className="text-md font-semibold text-yellow-800 mb-3">
                  Duplicate Transactions ({result.duplicates})
                </h4>
                <p className="text-xs text-yellow-700 mb-3">
                  These transactions were already imported:
                </p>
                <div className="max-h-48 overflow-y-auto">
                  <ul className="space-y-1">
                    {result.duplicateTransactions.slice(0, 10).map((dup, index) => (
                      <li key={index} className="text-xs text-yellow-800 bg-yellow-100 p-2 rounded">
                        {dup}
                      </li>
                    ))}
                    {result.duplicateTransactions.length > 10 && (
                      <li className="text-xs text-yellow-700 italic p-2">
                        ... and {result.duplicateTransactions.length - 10} more
                      </li>
                    )}
                  </ul>
                </div>
              </div>
            )}
          </div>
        )}

        {/* Expected Format Info */}
        <div className="mt-8 p-4 bg-gray-50 rounded-md">
          <h3 className="font-semibold text-gray-800 mb-2">Expected File Format:</h3>
          <p className="text-sm text-gray-600 mb-3">
            Your credit card statement should have these columns:
          </p>
          <div className="overflow-x-auto">
            <table className="min-w-full text-xs border border-gray-300">
              <thead className="bg-gray-200">
                <tr>
                  <th className="border border-gray-300 px-2 py-1">Transaction Type</th>
                  <th className="border border-gray-300 px-2 py-1">Customer Name</th>
                  <th className="border border-gray-300 px-2 py-1">Date</th>
                  <th className="border border-gray-300 px-2 py-1">Description</th>
                  <th className="border border-gray-300 px-2 py-1">Amount</th>
                  <th className="border border-gray-300 px-2 py-1">Debit/Credit</th>
                </tr>
              </thead>
              <tbody>
                <tr>
                  <td className="border border-gray-300 px-2 py-1">Domestic</td>
                  <td className="border border-gray-300 px-2 py-1">PRANAY RAUT</td>
                  <td className="border border-gray-300 px-2 py-1">19/10/2025</td>
                  <td className="border border-gray-300 px-2 py-1">IGST-VPS2629397617117</td>
                  <td className="border border-gray-300 px-2 py-1">167.22</td>
                  <td className="border border-gray-300 px-2 py-1"></td>
                </tr>
                <tr>
                  <td className="border border-gray-300 px-2 py-1">Domestic</td>
                  <td className="border border-gray-300 px-2 py-1">PRANAY RAUT</td>
                  <td className="border border-gray-300 px-2 py-1">20/10/2025</td>
                  <td className="border border-gray-300 px-2 py-1">TELE TRANSFER CREDIT</td>
                  <td className="border border-gray-300 px-2 py-1">11180.00</td>
                  <td className="border border-gray-300 px-2 py-1">Cr</td>
                </tr>
              </tbody>
            </table>
          </div>
          <p className="text-xs text-gray-500 mt-2">
            💡 Transactions will be automatically categorized using your existing rules
          </p>
        </div>
      </div>
    </div>
  )
}

export default CreditCardUploadPage

