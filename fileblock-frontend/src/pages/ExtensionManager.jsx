import { useEffect, useState } from "react";
import {
  getFixedExtensions,
  updateFixedExtension,
  getCustomExtensions,
  addCustomExtension,
  removeCustomExtension,
} from "../api/extensionApi";

const MAX_CUSTOM = 200;

export default function ExtensionManager() {
  const [fixedExtensions, setFixedExtensions] = useState([]);
  const [customExtensions, setCustomExtensions] = useState([]);
  const [inputExt, setInputExt] = useState("");
  const [errorMessage, setErrorMessage] = useState("");

  // ✅ 초기 로딩
  useEffect(() => {
    getFixedExtensions()
      .then((res) => {
        if (res.data.success) setFixedExtensions(res.data.data || []);
        else setErrorMessage(res.data.error.message);
      })
      .catch(() => setErrorMessage("고정 확장자 조회 실패"));

    getCustomExtensions()
      .then((res) => {
        if (res.data.success) setCustomExtensions(res.data.data || []);
        else setErrorMessage(res.data.error.message);
      })
      .catch(() => setErrorMessage("커스텀 확장자 조회 실패"));
  }, []);

  // ✅ 고정 확장자 토글
  const toggleFixed = (ext) => {
    updateFixedExtension(ext.seq, !ext.isBlocked)
      .then((res) => {
        if (res.data.success) {
          const updated = res.data.data;
          setFixedExtensions((prev) =>
            prev.map((it) => (it.seq === ext.seq ? updated : it))
          );
        } else {
          setErrorMessage(res.data.error.message);
        }
      })
      .catch(() => setErrorMessage("고정 확장자 업데이트 실패"));
  };

  // ✅ 커스텀 확장자 추가
  const handleAdd = () => {
    const value = inputExt.trim();
    if (!value) return;

    if (customExtensions.length >= MAX_CUSTOM) {
      setErrorMessage(`커스텀 확장자는 최대 ${MAX_CUSTOM}개까지 가능합니다.`);
      return;
    }

    addCustomExtension(value)
      .then((res) => {
        if (res.data.success) {
          setCustomExtensions((prev) => [...prev, res.data.data]);
          setInputExt("");
        } else {
          setErrorMessage(res.data.error.message);
        }
      })
      .catch(() => setErrorMessage("커스텀 확장자 추가 실패"));
  };

  const onKeyDown = (e) => {
    if (e.key === "Enter") {
      e.preventDefault();
      handleAdd();
    }
  };

  // ✅ 커스텀 확장자 삭제
  const handleDelete = (seq) => {
    removeCustomExtension(seq)
      .then((res) => {
        if (res.data.success) {
          setCustomExtensions((prev) => prev.filter((it) => it.seq !== seq));
        } else {
          setErrorMessage(res.data.error.message);
        }
      })
      .catch(() => setErrorMessage("커스텀 확장자 삭제 실패"));
  };

  const atLimit = customExtensions.length >= MAX_CUSTOM;

  return (
    <div className="flex items-center justify-center min-h-screen bg-gray-100">
      <div className="w-full max-w-4xl bg-white rounded-lg shadow p-8">
        <h2 className="text-xl font-bold mb-2 flex items-center gap-2">
          <span className="text-yellow-500">📂</span> 파일 확장자 차단
        </h2>
        <p className="text-sm text-gray-600 mb-6">
          파일확장자에 따라 특정 형식의 파일을 첨부/전송하지 못하도록 제한
        </p>

        {/* 에러 메시지 출력 */}
        {errorMessage && (
          <div className="mb-4 p-3 rounded bg-red-100 text-red-700 text-sm">
            ❌ {errorMessage}
          </div>
        )}

        {/* ✅ 고정 확장자 */}
        <section className="mb-6">
          <h3 className="font-semibold mb-2">① 고정 확장자</h3>
          <div className="flex gap-3 flex-wrap">
            {fixedExtensions.map((ext) => (
              <label key={ext.seq} className="flex items-center gap-1">
                <input
                  type="checkbox"
                  checked={ext.isBlocked}
                  onChange={() => toggleFixed(ext)}
                  className="w-4 h-4"
                />
                {ext.extension}
              </label>
            ))}
          </div>
        </section>

        {/* ✅ 커스텀 확장자: 높이·폭 고정, 내부만 세로 스크롤 */}
        <section>
          <h3 className="font-semibold mb-2">② 커스텀 확장자</h3>

          <div className="flex gap-2 mb-2">
            <input
              type="text"
              value={inputExt}
              onChange={(e) => setInputExt(e.target.value)}
              onKeyDown={onKeyDown}
              placeholder="확장자 입력"
              className="flex-1 border rounded px-3 py-2"
            />
            <button
              onClick={handleAdd}
              disabled={atLimit}
              className={`px-4 py-2 rounded ${
                atLimit
                  ? "bg-gray-200 text-gray-400 cursor-not-allowed"
                  : "bg-gray-100 hover:bg-gray-200"
              }`}
              title={atLimit ? `최대 ${MAX_CUSTOM}개` : ""}
            >
              +추가
            </button>
          </div>

          <div className="border rounded p-3">
            {/* relative: 카운트 절대배치 (레이아웃 영향 X) */}
            <div className="relative">
              {/* 
                핵심: 높이 고정 + 내부 스크롤 + 스크롤바 공간 예약
                - min-h/max-h 를 동일하게 → 완전 고정
                - [@scrollbar-gutter:stable] : 폭/레이아웃 흔들림 방지
              */}
              <div className="min-h-[96px] max-h-[96px] overflow-y-auto pr-2 [@scrollbar-gutter:stable]">
                <div className="grid grid-cols-10 gap-2">
                  {customExtensions.length === 0 ? (
                    <div className="col-span-10 text-xs text-gray-400">
                      커스텀 확장자를 추가해 주세요.
                    </div>
                  ) : (
                    customExtensions.map((ext) => (
                      <span
                        key={ext.seq}
                        className="h-7 px-2 bg-gray-200 rounded flex items-center justify-between gap-1 text-xs leading-none"
                        title={ext.extension}
                      >
                        <span className="truncate">{ext.extension}</span>
                        <button
                          onClick={() => handleDelete(ext.seq)}
                          className="text-red-500 ml-1"
                          aria-label={`${ext.extension} 삭제`}
                        >
                          ✕
                        </button>
                      </span>
                    ))
                  )}
                </div>
              </div>

              {/* 우상단 카운트 (흐름 차지 X) */}
              <div className="absolute top-0 right-0 text-[11px] text-gray-500">
                {customExtensions.length}/{MAX_CUSTOM}
              </div>
            </div>
          </div>
        </section>
      </div>
    </div>
  );
}
