import { createContext, useCallback, useContext, useState } from 'react'

const ToastContext = createContext(null)

let idSeq = 0

export function ToastProvider({ children }) {
  const [toasts, setToasts] = useState([])

  const remove = useCallback((id) => {
    setToasts((prev) => prev.filter((t) => t.id !== id))
  }, [])

  const push = useCallback(
    (message, type = 'info') => {
      const id = ++idSeq
      setToasts((prev) => [...prev, { id, message, type }])
      setTimeout(() => remove(id), 3500)
    },
    [remove]
  )

  const toast = {
    success: (m) => push(m, 'success'),
    error: (m) => push(m, 'error'),
    info: (m) => push(m, 'info'),
  }

  return (
    <ToastContext.Provider value={toast}>
      {children}
      <div className="fixed top-4 right-4 z-50 flex flex-col gap-2 w-[calc(100%-2rem)] max-w-sm">
        {toasts.map((t) => (
          <ToastItem key={t.id} {...t} onClose={() => remove(t.id)} />
        ))}
      </div>
    </ToastContext.Provider>
  )
}

function ToastItem({ message, type, onClose }) {
  const styles = {
    success: 'bg-white border-l-4 border-emerald-500 text-ink',
    error: 'bg-white border-l-4 border-red-500 text-ink',
    info: 'bg-white border-l-4 border-steel text-ink',
  }
  const icon = { success: '✓', error: '✕', info: 'ℹ' }
  const iconColor = {
    success: 'text-emerald-500',
    error: 'text-red-500',
    info: 'text-steel',
  }
  return (
    <div
      className={`flex items-start gap-3 px-4 py-3 rounded-lg shadow-lg border border-slate-100 ${styles[type]} animate-[fadeIn_0.15s_ease-out]`}
      role="status"
    >
      <span className={`font-bold ${iconColor[type]}`}>{icon[type]}</span>
      <p className="text-sm flex-1">{message}</p>
      <button onClick={onClose} className="text-slate-400 hover:text-slate-600 text-sm">
        ✕
      </button>
    </div>
  )
}

export function useToast() {
  const ctx = useContext(ToastContext)
  if (!ctx) throw new Error('useToast debe usarse dentro de ToastProvider')
  return ctx
}
