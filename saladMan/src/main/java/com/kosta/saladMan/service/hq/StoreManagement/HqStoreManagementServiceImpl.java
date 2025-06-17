package com.kosta.saladMan.service.hq.StoreManagement;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.kosta.saladMan.dto.store.StoreDto;
import com.kosta.saladMan.entity.store.Store;
import com.kosta.saladMan.repository.StoreRepository;
import com.kosta.saladMan.repository.storeManagement.StoreDslRepository;

@Service
public class HqStoreManagementServiceImpl implements HqStoreManagementService {

	@Autowired
	private StoreRepository storeRepository;

	@Autowired
	private StoreDslRepository storeDslRepository;

	@Autowired
	private BCryptPasswordEncoder bCryptPasswordEncoder;

	@Override
	public void storeRegister(StoreDto storeDto) throws Exception {
		Optional<Store> oStore = storeRepository.findByUsername(storeDto.getUsername());

		if (oStore.isPresent()) {
			throw new Exception("이미 존재하는 아이디");
		}
		storeDto.setPassword(bCryptPasswordEncoder.encode(storeDto.getPassword()));
		storeDto.setRole("ROLE_STORE");
		storeRepository.save(storeDto.toEntity());
	}

	@Override
	public Boolean isStoreNameDouble(String storeName) throws Exception {
		Optional<Store> oStore = storeRepository.findByName(storeName);
		if (oStore.isPresent()) {
			return true;
		}
		return false;
	}

	@Override
	public Boolean isStoreUsernameDouble(String storeUsername) throws Exception {
		Optional<Store> oStore = storeRepository.findByUsername(storeUsername);
		if (oStore.isPresent()) {
			return true;
		}
		return false;
	}

	@Override
	public Page<StoreDto> searchStores(String location, String status, String keyword, Pageable pageable) {
		return storeDslRepository.searchStores(location, status, keyword, pageable);

	}

	@Override
	public StoreDto getStoreDetail(Integer id) throws Exception {
		Store store = storeRepository.findById(id).orElseThrow(() -> new Exception("매장을 찾을 수 없습니다."));
		StoreDto storeDto = store.toDto();
		storeDto.setPassword(null); // 비밀번호 제거
		return storeDto;
	}

}
