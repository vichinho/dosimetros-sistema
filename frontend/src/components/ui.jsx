// Componentes de UI reutilizables, estilo claro minimalista.

export function Card({ title, action, children, className = '' }) {
  return (
    <div className={`bg-white rounded-2xl border border-mist/60 p-5 sm:p-6 ${className}`}>
      {(title || action) && (
        <div className="flex items-center justify-between mb-4">
          {title && <h2 className="text-base font-semibold text-ink">{title}</h2>}
          {action}
        </div>
      )}
      {children}
    </div>
  )
}

export function Button({ children, variant = 'primary', className = '', ...props }) {
  const styles = {
    primary: 'bg-steel hover:bg-steel/90 text-white shadow-sm',
    secondary: 'bg-white hover:bg-cream text-ink border border-mist',
    danger: 'bg-red-600 hover:bg-red-700 text-white shadow-sm',
    ghost: 'text-ink hover:bg-mist/30',
  }
  return (
    <button
      className={`inline-flex items-center justify-center gap-2 px-4 py-2 rounded-lg text-sm font-medium transition disabled:opacity-50 disabled:cursor-not-allowed ${styles[variant]} ${className}`}
      {...props}
    >
      {children}
    </button>
  )
}

export function Input({ label, ...props }) {
  return (
    <label className="block">
      {label && <span className="block text-sm font-medium text-ink/70 mb-1.5">{label}</span>}
      <input
        className="w-full px-3 py-2 border border-mist rounded-lg text-sm focus:outline-none focus:ring-2 focus:ring-steel/40 focus:border-steel transition"
        {...props}
      />
    </label>
  )
}

export function Select({ label, children, ...props }) {
  return (
    <label className="block">
      {label && <span className="block text-sm font-medium text-ink/70 mb-1.5">{label}</span>}
      <select
        className="w-full px-3 py-2 border border-mist rounded-lg text-sm bg-white focus:outline-none focus:ring-2 focus:ring-steel/40 focus:border-steel transition"
        {...props}
      >
        {children}
      </select>
    </label>
  )
}

export function Alert({ type = 'info', children }) {
  if (!children) return null
  const styles = {
    info: 'bg-blue-50 text-blue-800 border-blue-200',
    success: 'bg-emerald-50 text-emerald-800 border-emerald-200',
    error: 'bg-red-50 text-red-800 border-red-200',
  }
  return <div className={`px-4 py-3 rounded-lg border text-sm ${styles[type]}`}>{children}</div>
}

export function Badge({ children, color = 'slate' }) {
  const colors = {
    slate: 'bg-mist/40 text-ink',
    green: 'bg-emerald-100 text-emerald-700',
    blue: 'bg-steel/20 text-steel',
    red: 'bg-red-100 text-red-700',
    amber: 'bg-amber-100 text-amber-700',
  }
  return (
    <span className={`inline-block px-2 py-0.5 rounded-full text-xs font-medium ${colors[color]}`}>
      {children}
    </span>
  )
}

export function Spinner({ className = '' }) {
  return (
    <div
      className={`inline-block w-5 h-5 border-2 border-mist border-t-steel rounded-full animate-spin ${className}`}
      role="status"
      aria-label="Cargando"
    />
  )
}

export function Loading({ label = 'Cargando…' }) {
  return (
    <div className="flex items-center justify-center gap-3 py-12 text-slate-400">
      <Spinner />
      <span className="text-sm">{label}</span>
    </div>
  )
}

export function EmptyState({ children }) {
  return <div className="py-10 text-center text-sm text-slate-400">{children}</div>
}
