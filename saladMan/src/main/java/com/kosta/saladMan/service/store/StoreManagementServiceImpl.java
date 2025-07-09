package com.kosta.saladMan.service.store;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.kosta.saladMan.dto.store.CloseStoreDto;
import com.kosta.saladMan.dto.store.ResetStorePasswordDto;
import com.kosta.saladMan.dto.store.StoreDto;
import com.kosta.saladMan.dto.store.StoreUpdateDto;
import com.kosta.saladMan.entity.store.Store;
import com.kosta.saladMan.repository.StoreRepository;
import com.kosta.saladMan.repository.storeManagement.StoreDslRepository;

@Service
public class StoreManagementServiceImpl implements StoreManagementService {

	@Autowired
	private StoreRepository storeRepository;

	@Autowired
	private StoreDslRepository storeDslRepository;


	
	@Autowired
	private PasswordEncoder passwordEncoder;


	@Override
	public Integer storeRegister(StoreDto storeDto) throws Exception {
		Optional<Store> oStore = storeRepository.findByUsername(storeDto.getUsername());
	    if (oStore.isPresent()) {
	        throw new Exception("이미 존재하는 아이디");
	    }
	    storeDto.setPassword(passwordEncoder.encode(storeDto.getPassword()));
	    storeDto.setRole("ROLE_STORE");
	    Store savedStore = storeRepository.save(storeDto.toEntity());
	    return savedStore.getId(); 
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

	@Override
	public Boolean updateStore(StoreUpdateDto storeUpdateDto) throws Exception {
		Optional<Store> optional = storeRepository.findById(storeUpdateDto.getId());
		if (optional.isEmpty())
			return false;

		Store store = optional.get();

		store.setName(storeUpdateDto.getName());
		store.setAddress(storeUpdateDto.getAddress());
		store.setPhoneNumber(storeUpdateDto.getPhoneNumber());
		store.setUsername(storeUpdateDto.getUsername());
		store.setLocation(storeUpdateDto.getLocation());
		store.setLatitude(storeUpdateDto.getLatitude());
		store.setLongitude(storeUpdateDto.getLongitude());
		store.setOpenTime(storeUpdateDto.getOpenTime());
		store.setCloseTime(storeUpdateDto.getCloseTime());
		store.setBreakDay(storeUpdateDto.getBreakDay());
		store.setUsername(storeUpdateDto.getUsername());
		store.setDeliveryDay(storeUpdateDto.getDeliveryDay());

		storeRepository.save(store);

		return true;
	}

	@Override
	public void resetStorePassword(Store admin, ResetStorePasswordDto dto) throws Exception {
		System.out.println(admin.getRole());
		if (!"ROLE_HQ".equals(admin.getRole())) {
			throw new AccessDeniedException("관리자 권한이 필요합니다.");
		}

		if (!passwordEncoder.matches(dto.getAdminPassword(), admin.getPassword())) {
			throw new IllegalArgumentException("관리자 비밀번호가 일치하지 않습니다.");
		}

		Store targetStore = storeRepository.findById(dto.getId())
				.orElseThrow(() -> new RuntimeException("매장을 찾을 수 없습니다."));

		String encodedPassword = passwordEncoder.encode(dto.getNewPassword());
		targetStore.setPassword(encodedPassword);
		storeRepository.save(targetStore);
	}

	public void closeStore(CloseStoreDto dto) {
	    Store store = storeRepository.findById(dto.getId())
	        .orElseThrow(() -> new RuntimeException("매장을 찾을 수 없습니다."));
	    store.setClosedAt(dto.getClosedAt()); //
	    storeRepository.save(store);
	}

}
