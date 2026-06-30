import { useEffect, useRef, useState } from 'react'

/**
 * Combobox buscable y estilizado: input + lista desplegable filtrable.
 * options: [{ value, label }]. Devuelve value al seleccionar.
 */
export default function Combobox({ label, options, value, onChange, placeholder = 'Escribe o selecciona…', required }) {
  const [query, setQuery] = useState('')
  const [open, setOpen] = useState(false)
  const [activo, setActivo] = useState(0)
  const ref = useRef(null)

  const seleccionado = options.find((o) => String(o.value) === String(value))

  useEffect(() => {
    const onClickFuera = (e) => {
      if (ref.current && !ref.current.contains(e.target)) setOpen(false)
    }
    document.addEventListener('mousedown', onClickFuera)
    return () => document.removeEventListener('mousedown', onClickFuera)
  }, [])

  const filtradas = (open && query
    ? options.filter((o) => o.label.toLowerCase().includes(query.toLowerCase()))
    : options
  ).slice(0, 50)

  const display = open ? query : seleccionado ? seleccionado.label : ''

  const elegir = (op) => {
    onChange(op.value)
    setQuery('')
    setOpen(false)
  }

  const onKeyDown = (e) => {
    if (!open) return
    if (e.key === 'ArrowDown') {
      e.preventDefault()
      setActivo((a) => Math.min(a + 1, filtradas.length - 1))
    } else if (e.key === 'ArrowUp') {
      e.preventDefault()
      setActivo((a) => Math.max(a - 1, 0))
    } else if (e.key === 'Enter') {
      e.preventDefault()
      if (filtradas[activo]) elegir(filtradas[activo])
    } else if (e.key === 'Escape') {
      setOpen(false)
    }
  }

  return (
    <label className="block relative" ref={ref}>
      {label && <span className="block text-sm font-medium text-ink/70 mb-1.5">{label}</span>}
      <input
        value={display}
        onChange={(e) => {
          setQuery(e.target.value)
          setOpen(true)
          setActivo(0)
          if (e.target.value === '') onChange('')
        }}
        onFocus={() => {
          setQuery('')
          setOpen(true)
          setActivo(0)
        }}
        onKeyDown={onKeyDown}
        placeholder={placeholder}
        required={required && !value}
        className="w-full px-3 py-2 border border-mist rounded-lg text-sm focus:outline-none focus:ring-2 focus:ring-steel/40 focus:border-steel transition"
        autoComplete="off"
      />
      {open && filtradas.length > 0 && (
        <ul className="absolute z-20 mt-1 w-full max-h-60 overflow-auto bg-white border border-mist rounded-lg shadow-lg">
          {filtradas.map((o, i) => (
            <li
              key={o.value}
              onMouseDown={(e) => {
                e.preventDefault()
                elegir(o)
              }}
              onMouseEnter={() => setActivo(i)}
              className={`px-3 py-2 text-sm cursor-pointer ${
                i === activo ? 'bg-steel/15 text-ink' : 'text-ink/80 hover:bg-cream'
              } ${String(o.value) === String(value) ? 'font-semibold' : ''}`}
            >
              {o.label}
            </li>
          ))}
        </ul>
      )}
    </label>
  )
}
