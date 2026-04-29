const iconMap = {
  home: '⌂',
  search: '⌕',
  calendar: '◫',
  users: '◌',
  mentor: '◎',
  star: '★',
  shield: '◈',
  group: '◌',
  money: '₹',
  rocket: '🚀',
  bot: '🤖',
  chart: '📊',
};

export function AppIcon({ name }) {
  return <span aria-hidden="true">{iconMap[name] || '•'}</span>;
}
