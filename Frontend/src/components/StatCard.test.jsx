import { render, screen } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { describe, expect, it, vi } from 'vitest';

import StatCard from './StatCard.jsx';

describe('StatCard', () => {
  const item = {
    icon: 'chart',
    value: '24',
    label: 'Active sessions',
    change: '+4 this week',
  };

  it('renders the stat value, label, and change text', () => {
    render(<StatCard item={item} />);

    expect(screen.getByRole('button', { name: /active sessions/i })).toBeInTheDocument();
    expect(screen.getByText('24')).toBeInTheDocument();
    expect(screen.getByText('+4 this week')).toBeInTheDocument();
  });

  it('calls onClick when selected by the user', async () => {
    const onClick = vi.fn();
    const user = userEvent.setup();

    render(<StatCard item={item} onClick={onClick} />);
    await user.click(screen.getByRole('button', { name: /active sessions/i }));

    expect(onClick).toHaveBeenCalledTimes(1);
  });

  it('adds the active class when the card is active', () => {
    render(<StatCard item={item} isActive />);

    expect(screen.getByRole('button', { name: /active sessions/i })).toHaveClass('stat-card-active');
  });
});
