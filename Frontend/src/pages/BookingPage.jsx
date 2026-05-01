import { useEffect, useMemo, useState } from 'react';
import { useSearchParams } from 'react-router-dom';

import SectionHeading from '../components/SectionHeading.jsx';
import {
  useBookSessionMutation,
  useGetMentorRatingSummaryQuery,
  useGetMentorsQuery,
  useGetReviewsByMentorQuery,
  useGetUsersQuery,
} from '../features/platform/platformApi.js';
import { buildUserMap, formatCurrency, mapMentorToCard } from '../utils/viewMappers.js';

const durations = [30, 60, 90];
const slots = ['09:00 AM', '10:30 AM', '11:00 AM', '02:00 PM', '04:30 PM'];
const getTodayDateInputValue = () => {
  const today = new Date();
  const year = today.getFullYear();
  const month = String(today.getMonth() + 1).padStart(2, '0');
  const day = String(today.getDate()).padStart(2, '0');

  return `${year}-${month}-${day}`;
};

function BookingPage({ currentUser }) {
  const [searchParams] = useSearchParams();
  const { data: mentorsResponse, isLoading: isMentorsLoading } = useGetMentorsQuery();
  const { data: usersResponse } = useGetUsersQuery();
  const [bookSession, { isLoading: isBooking }] = useBookSessionMutation();
  const [selectedMentorId, setSelectedMentorId] = useState('');
  const [selectedDate, setSelectedDate] = useState('');
  const [selectedDuration, setSelectedDuration] = useState(60);
  const [selectedSlot, setSelectedSlot] = useState(slots[2]);
  const [topic, setTopic] = useState('');
  const [requiredSkill, setRequiredSkill] = useState('');
  const [message, setMessage] = useState('');
  const [errorMessage, setErrorMessage] = useState('');
  const todayDate = getTodayDateInputValue();

  const userMap = useMemo(() => buildUserMap(usersResponse?.content || []), [usersResponse]);
  const mentors = useMemo(
    () => (mentorsResponse?.content || []).map((mentor) => mapMentorToCard(mentor, userMap)),
    [mentorsResponse, userMap]
  );

  useEffect(() => {
    if (!mentors.length) {
      return;
    }

    const mentorFromUrl = searchParams.get('mentorId');
    const matchedMentor = mentors.find((mentor) => String(mentor.id) === String(mentorFromUrl));
    const nextMentor = matchedMentor || mentors[0];
    // This initializes form defaults after async mentor data arrives.
    // eslint-disable-next-line react-hooks/set-state-in-effect
    setSelectedMentorId(String(nextMentor.id));
    setRequiredSkill((current) => current || nextMentor.skills[0] || '');
    setTopic((current) => current || `Mentorship session with ${nextMentor.name}`);
  }, [mentors, searchParams]);

  const selectedMentor = mentors.find((mentor) => String(mentor.id) === String(selectedMentorId)) || mentors[0];
  const { data: ratingSummary } = useGetMentorRatingSummaryQuery(selectedMentor?.id, {
    skip: !selectedMentor?.id,
  });
  const { data: reviewsResponse } = useGetReviewsByMentorQuery(
    { mentorId: selectedMentor?.id, page: 0, size: 5, sortBy: 'id' },
    { skip: !selectedMentor?.id }
  );

  const getFriendlyBookingError = (error) => {
    const rawMessage = error?.data?.message || error?.data?.error || error?.message || 'Booking failed.';

    if (rawMessage.includes('not approved yet') || rawMessage.includes('status: REJECTED')) {
      return 'This mentor is not available for booking right now because their mentor profile is not approved yet.';
    }

    if (rawMessage.includes('not currently available')) {
      return 'This mentor has paused bookings right now. Please choose another mentor or try again later.';
    }

    if (rawMessage.includes('cannot be booked for skill')) {
      return 'Please choose one of the listed skills that this mentor actually offers.';
    }

    return rawMessage;
  };

  const handleSubmit = async () => {
    if (!selectedMentor?.id || !selectedDate || !topic.trim() || !requiredSkill) {
      setErrorMessage('Please choose a mentor, matching skill, date, and topic before booking.');
      return;
    }

    if (selectedDate < todayDate) {
      setErrorMessage('Please choose today or a future date.');
      return;
    }

    try {
      setErrorMessage('');
      setMessage('');

      const bookedSession = await bookSession({
        mentorId: selectedMentor.id,
        learnerId: Number(currentUser.userId),
        sessionDate: selectedDate,
        duration: selectedDuration,
        topic: topic.trim(),
        requiredSkill,
      }).unwrap();

      setMessage(
        `Session requested successfully for ${selectedDate} at ${selectedSlot} for ${requiredSkill}. Join link: https://meet.jit.si/skillsync-session-${bookedSession.id}`
      );
    } catch (error) {
      setErrorMessage(getFriendlyBookingError(error));
    }
  };

  return (
    <div className="page-stack">
      <SectionHeading
        title="📅 Book a Session"
        subtitle={
          selectedMentor ? `Schedule a 1-on-1 session with ${selectedMentor.name}` : 'Schedule a 1-on-1 session'
        }
      />

      <div className="booking-layout">
        <section className="card booking-card">
          <div className="stepper">
            <div className="step active">1. Pick Mentor</div>
            <div className="step active">2. Choose Date</div>
            <div className="step active">3. Confirm</div>
          </div>

          {isMentorsLoading ? <div>Loading mentors...</div> : null}

          <div className="booking-section">
            <h4>👨‍🏫 Select Mentor</h4>
            <select
              value={selectedMentorId}
              onChange={(event) => {
                const nextMentor = mentors.find((mentor) => String(mentor.id) === event.target.value);
                setSelectedMentorId(event.target.value);
                setRequiredSkill(nextMentor?.skills[0] || '');
              }}
            >
              {mentors.map((mentor) => (
                <option key={mentor.id} value={mentor.id}>
                  {mentor.name} - {mentor.price}
                </option>
              ))}
            </select>
          </div>

          <div className="booking-section">
            <h4>🎯 Required Skill Match</h4>
            <select value={requiredSkill} onChange={(event) => setRequiredSkill(event.target.value)}>
              <option value="">Select one of this mentor&apos;s skills</option>
              {(selectedMentor?.skills || []).map((skill) => (
                <option key={skill} value={skill}>
                  {skill}
                </option>
              ))}
            </select>
            <p className="muted-text">Booking is allowed only for a skill this mentor already offers.</p>
          </div>

          <div className="booking-section">
            <h4>📅 Select Date</h4>
            <input
              type="date"
              min={todayDate}
              value={selectedDate}
              onChange={(event) => setSelectedDate(event.target.value)}
            />
          </div>

          <div className="booking-section">
            <h4>⏰ Preferred Slot</h4>
            <div className="chip-row">
              {slots.map((slot) => (
                <button
                  className={slot === selectedSlot ? 'slot-chip active' : 'slot-chip'}
                  key={slot}
                  onClick={() => setSelectedSlot(slot)}
                  type="button"
                >
                  {slot}
                </button>
              ))}
            </div>
          </div>

          <div className="booking-section">
            <h4>⏳ Session Duration</h4>
            <div className="chip-row">
              {durations.map((duration) => (
                <button
                  className={duration === selectedDuration ? 'slot-chip active' : 'slot-chip'}
                  key={duration}
                  onClick={() => setSelectedDuration(duration)}
                  type="button"
                >
                  {duration} min
                </button>
              ))}
            </div>
          </div>

          <div className="booking-section">
            <h4>📝 Session Topic</h4>
            <input type="text" value={topic} onChange={(event) => setTopic(event.target.value)} />
          </div>
        </section>

        <aside className="card summary-card">
          {selectedMentor ? (
            <>
              <div className="mentor-header">
                <div className="avatar-badge warm">{selectedMentor.initials}</div>
                <div>
                  <h3>{selectedMentor.name}</h3>
                  <p>{selectedMentor.experience}</p>
                </div>
              </div>

              <div className="summary-list">
                <div><span>Date</span><strong>{selectedDate || 'Select a date'}</strong></div>
                <div><span>Time</span><strong>{selectedSlot}</strong></div>
                <div><span>Duration</span><strong>{selectedDuration} minutes</strong></div>
                <div><span>Topic</span><strong>{topic || 'Add topic'}</strong></div>
                <div><span>Skill Match</span><strong>{requiredSkill || 'Choose a mentor skill'}</strong></div>
                <div><span>Rate</span><strong>{selectedMentor.price}</strong></div>
                <div>
                  <span>Rating</span>
                  <strong>
                    {ratingSummary?.averageRating ? ratingSummary.averageRating.toFixed(1) : selectedMentor.rating}
                    {' '}({ratingSummary?.totalReviews ?? selectedMentor.reviews} reviews)
                  </strong>
                </div>
              </div>

              <div className="summary-total">
                <span>Total</span>
                <strong>{formatCurrency(selectedMentor.raw.hourlyRate * (selectedDuration / 60))}</strong>
              </div>
            </>
          ) : (
            <div>Select a mentor to continue.</div>
          )}

          {message ? <p className="success-text">{message}</p> : null}
          {errorMessage ? <p className="muted-text">{errorMessage}</p> : null}

          <button className="primary-button large-button" onClick={handleSubmit} type="button" disabled={isBooking}>
            {isBooking ? 'Confirming...' : '✅ Confirm Booking'}
          </button>
        </aside>
      </div>

      <section className="card dashboard-panel">
        <SectionHeading
          title="⭐ Recent Mentor Reviews"
          subtitle="Live reviews pulled from the review service."
        />
        <div className="detail-list">
          {(reviewsResponse?.content || []).map((review) => (
            <article className="detail-item" key={review.id}>
              <div>
                <h3>{'★'.repeat(review.rating)}</h3>
                <p>{review.comment || 'No written comment added.'}</p>
              </div>
              <div className="detail-meta">
                <strong>Session #{review.sessionId}</strong>
                <span>Learner #{review.learnerId}</span>
              </div>
            </article>
          ))}
          {!reviewsResponse?.content?.length ? <div className="activity-item">No reviews found for this mentor yet.</div> : null}
        </div>
      </section>
    </div>
  );
}

export default BookingPage;
