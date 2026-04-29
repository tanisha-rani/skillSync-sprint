function SessionTable({ sessions, participantLabel = 'Mentor', renderActions }) {
  return (
    <div className="card table-card">
      <table>
        <thead>
          <tr>
            <th>Date & Time</th>
            <th>{participantLabel}</th>
            <th>Topic</th>
            <th>Duration</th>
            <th>Status</th>
            {renderActions ? <th>Actions</th> : null}
          </tr>
        </thead>
        <tbody>
          {sessions.map((session) => (
            <tr key={`${session.date}-${session.time}-${session.mentor}`}>
              <td>
                {session.date} · {session.time}
              </td>
              <td>{session.mentor}</td>
              <td>{session.topic}</td>
              <td>{session.duration}</td>
              <td>
                <span className={`table-status ${session.status.toLowerCase()}`}>{session.status}</span>
              </td>
              {renderActions ? <td>{renderActions(session)}</td> : null}
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
}

export default SessionTable;
